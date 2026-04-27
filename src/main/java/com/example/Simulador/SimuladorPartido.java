package com.example.Simulador;

import com.example.*;
import com.example.Simulador.Ocasion.TipoOcasion;
import com.example.Simulador.Evento.*;

import java.util.*;

/**
 * SimuladorPartido v2
 *
 * Pipeline por minuto:
 *   1. Actualizar energía (decaimiento progresivo)
 *   2. Actualizar momentum (decaimiento natural)
 *   3. Calcular posesión (basada en medio + energía + momentum)
 *   4. Generar ataques por equipo (Poisson, target 8–15 por partido)
 *   5. Convertir ataques en ocasiones (20–30% según ataque vs defensa)
 *   6. Calcular xG por ocasión (0.04–0.40, según tipo y ataque vs defensa)
 *   7. Resolver gol únicamente con prob = xG
 *   8. Actualizar momentum ante goles / ocasiones claras
 *
 * Targets:
 *   - Goles totales por partido: ~2.0 – 3.0
 *   - Ocasiones totales por partido: ~20 – 30
 *   - Máximo 2 ocasiones por minuto (suma de ambos equipos)
 */
public class SimuladorPartido {

    // ─── Duración ──────────────────────────────────────────────────────────
    private static final int MINUTOS_PARTIDO = 90;

    // ─── Energía ───────────────────────────────────────────────────────────
    /** Descenso de energía por minuto. Al min 90 la energía habrá bajado ~0.36. */
    private static final double DECAIMIENTO_ENERGIA = 0.004;

    // ─── Posesión ──────────────────────────────────────────────────────────
    private static final double PESO_MOMENTUM_POSESION = 0.10;
    private static final double PESO_ENERGIA_POSESION  = 0.06;
    private static final double RUIDO_POSESION         = 0.05;
    private static final double POSESION_MIN           = 0.25;
    private static final double POSESION_MAX           = 0.75;

    // ─── Generación de ataques ─────────────────────────────────────────────
    /**
     * Lambda = (medio * posesion * energia) / DIVISOR_ATAQUES
     * Con medio=50, posesion=0.5, energia_prom=0.82 → ~62 ataques/partido/equipo.
     * Tras conversión (prob~15%) → ~10–13 ocasiones/equipo → 20–25 totales.
     */
    private static final double DIVISOR_ATAQUES = 35.0;

    // ─── Conversión ataque → ocasión ──────────────────────────────────────
    /**
     * Prob. efectiva = PROB_OCASION_BASE * ratio(ataque / (ataque + defensa))
     * Con equipos normales (ratio ≈ 0.40–0.60) → prob ≈ 12%–18%.
     */
    private static final double PROB_OCASION_BASE = 0.27;

    // ─── xG base por tipo de ocasión ──────────────────────────────────────
    private static final double XG_CONTRAATAQUE_BASE      = 0.22;
    private static final double XG_ATAQUE_POSICIONAL_BASE = 0.13;
    private static final double XG_TIRO_LEJANO_BASE       = 0.03;
    private static final double XG_PELOTA_PARADA_BASE     = 0.14;
    private static final double XG_VARIACION              = 0.10; // ±20%
    private static final double XG_MIN                    = 0.04;
    private static final double XG_MAX                    = 0.40;

    // ─── Probabilidades acumuladas de cada tipo de ocasión ────────────────
    // TIRO_LEJANO 40% | POSICIONAL 35% | PARADA 15% | CONTRAATAQUE 10%
    private static final double PESO_TIRO_LEJANO   = 0.30;
    private static final double PESO_POSICIONAL    = 0.70;
    private static final double PESO_PELOTA_PARADA = 0.88;

    // ─── Límite de ocasiones por minuto (total ambos equipos) ─────────────
    private static final int MAX_OCASIONES_POR_MINUTO = 1;

    // ─── Momentum ─────────────────────────────────────────────────────────
    private static final double DECAY_MOMENTUM         = 0.94;
    private static final double MOMENTUM_GOL_PROPIO    = 0.30;
    private static final double MOMENTUM_GOL_RECIBIDO  = 0.20;
    private static final double MOMENTUM_OCASION_CLARA = 0.08;
    private static final double XG_UMBRAL_CLARA        = 0.25;

    // ─── Random centralizado ──────────────────────────────────────────────
    private final Random random = new Random();

    // ══════════════════════════════════════════════════════════════════════
    //  MÉTODO PÚBLICO
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Simula un partido completo y devuelve la lista de eventos cronológica.
     */
    public List<Evento> simular(Equipo local, Equipo visitante) {
        EstadoPartido estado = new EstadoPartido(local, visitante);
        List<Evento> eventos = new ArrayList<>();
        double acumPosesionLocal = 0.0;

        for (int minuto = 1; minuto <= MINUTOS_PARTIDO; minuto++) {
            estado.setMinuto(minuto);

            // 1. Estado dinámico
            actualizarEnergia(estado);
            actualizarMomentum(estado);

            // 2. Posesión del minuto
            double posesion = calcularPosesion(estado);
            estado.setPosesionLocal(posesion);
            acumPosesionLocal += posesion;

            // 3. Ataques de cada equipo
            int ataquesLocal = generarAtaques(local, posesion, estado.getEnergiaLocal());
            int ataquesVisit = generarAtaques(visitante, 1.0 - posesion, estado.getEnergiaVisitante());

            // 4–7. Ataques → ocasiones → goles (límite por minuto compartido)
            int ocasionesMinuto = 0;

            // Orden aleatorio para evitar sesgo local/visitante
            if (random.nextBoolean()) {
                ocasionesMinuto += procesarAtaques(estado, local, visitante,
                        ataquesLocal, ocasionesMinuto, eventos);
                procesarAtaques(estado, visitante, local,
                        ataquesVisit, ocasionesMinuto, eventos);
            } else {
                ocasionesMinuto += procesarAtaques(estado, visitante, local,
                        ataquesVisit, ocasionesMinuto, eventos);
                procesarAtaques(estado, local, visitante,
                        ataquesLocal, ocasionesMinuto, eventos);
            }
        }

        imprimirResumen(estado, acumPosesionLocal, eventos);
        return eventos;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  1. ENERGÍA
    // ══════════════════════════════════════════════════════════════════════

    private void actualizarEnergia(EstadoPartido estado) {
        estado.setEnergiaLocal(estado.getEnergiaLocal() - DECAIMIENTO_ENERGIA);
        estado.setEnergiaVisitante(estado.getEnergiaVisitante() - DECAIMIENTO_ENERGIA);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  2. MOMENTUM
    // ══════════════════════════════════════════════════════════════════════

    private void actualizarMomentum(EstadoPartido estado) {
        estado.setMomentumLocal(estado.getMomentumLocal() * DECAY_MOMENTUM);
        estado.setMomentumVisitante(estado.getMomentumVisitante() * DECAY_MOMENTUM);
    }

    private void aplicarMomentumGol(EstadoPartido estado, Equipo marcador) {
        if (estado.esLocal(marcador)) {
            estado.setMomentumLocal(estado.getMomentumLocal() + MOMENTUM_GOL_PROPIO);
            estado.setMomentumVisitante(estado.getMomentumVisitante() - MOMENTUM_GOL_RECIBIDO);
        } else {
            estado.setMomentumVisitante(estado.getMomentumVisitante() + MOMENTUM_GOL_PROPIO);
            estado.setMomentumLocal(estado.getMomentumLocal() - MOMENTUM_GOL_RECIBIDO);
        }
    }

    private void aplicarMomentumOcasionClara(EstadoPartido estado, Equipo atacante) {
        if (estado.esLocal(atacante)) {
            estado.setMomentumLocal(estado.getMomentumLocal() + MOMENTUM_OCASION_CLARA);
        } else {
            estado.setMomentumVisitante(estado.getMomentumVisitante() + MOMENTUM_OCASION_CLARA);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  3. POSESIÓN
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Posesión = f(medio, momentum, energía) + ruido leve.
     *
     * El mediocampo es el factor dominante; momentum y energía son secundarios.
     * El ruido acotado garantiza que la posesión sea estable pero no constante.
     */
    private double calcularPosesion(EstadoPartido estado) {
        Equipo local     = estado.getLocal();
        Equipo visitante = estado.getVisitante();

        double base = (double) local.getMedio() / (local.getMedio() + visitante.getMedio());

        double diffMomentum = estado.getMomentumLocal() - estado.getMomentumVisitante();
        double diffEnergia  = estado.getEnergiaLocal()  - estado.getEnergiaVisitante();

        double ajuste = diffMomentum * PESO_MOMENTUM_POSESION
                      + diffEnergia  * PESO_ENERGIA_POSESION;

        double ruido = (random.nextDouble() - 0.5) * RUIDO_POSESION;

        return clamp(base + ajuste + ruido, POSESION_MIN, POSESION_MAX);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  4. GENERACIÓN DE ATAQUES (Poisson)
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Ataques de un equipo en un minuto ≈ Poisson(lambda).
     * Lambda escala con medio, posesión y energía.
     */
    private int generarAtaques(Equipo equipo, double posesion, double energia) {
        double lambda = (equipo.getMedio() * posesion * energia) / DIVISOR_ATAQUES;
        return muestraPoisson(lambda);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  5. PROCESAMIENTO ATAQUES → OCASIONES → GOLES
    // ══════════════════════════════════════════════════════════════════════

    private int procesarAtaques(EstadoPartido estado,
                                Equipo atacante,
                                Equipo defensor,
                                int ataques,
                                int ocasionesYaEnMinuto,
                                List<Evento> eventos) {
        int ocasionesGeneradas = 0;

        for (int i = 0; i < ataques; i++) {

            // Límite de densidad por minuto
            if (ocasionesYaEnMinuto + ocasionesGeneradas >= MAX_OCASIONES_POR_MINUTO) {
                break;
            }

            if (random.nextDouble() < calcularProbOcasion(atacante, defensor)) {

                TipoOcasion tipo = sortearTipoOcasion();
                double xG = calcularXG(tipo, atacante, defensor);

                Ocasion ocasion = new Ocasion(atacante, xG, tipo, estado.getMinuto());
                EventoOcasion eventoOcasion = new EventoOcasion(ocasion);
                eventoOcasion.aplicar(estado);
                eventos.add(eventoOcasion);
                ocasionesGeneradas++;

                // Ocasión clara → pequeño impulso de momentum
                if (xG >= XG_UMBRAL_CLARA) {
                    aplicarMomentumOcasionClara(estado, atacante);
                }

                // Gol: prob = xG (sin multiplicar ataque/defensa)
                if (esGol(xG)) {
                    EventoGol gol = new EventoGol(atacante, estado.getMinuto());
                    gol.aplicar(estado);
                    eventos.add(gol);
                    aplicarMomentumGol(estado, atacante);
                }
            }
        }

        return ocasionesGeneradas;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  5a. PROB ATAQUE → OCASIÓN
    // ══════════════════════════════════════════════════════════════════════

    private double calcularProbOcasion(Equipo atacante, Equipo defensor) {
        double ratio = (double) atacante.getAtaque()
                / (atacante.getAtaque() + defensor.getDefensa());
        return PROB_OCASION_BASE * ratio;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  5b. TIPO DE OCASIÓN
    // ══════════════════════════════════════════════════════════════════════

    private TipoOcasion sortearTipoOcasion() {
        double r = random.nextDouble();
        if (r < PESO_TIRO_LEJANO)   return TipoOcasion.TIRO_LEJANO;
        if (r < PESO_POSICIONAL)    return TipoOcasion.ATAQUE_POSICIONAL;
        if (r < PESO_PELOTA_PARADA) return TipoOcasion.PELOTA_PARADA;
        return TipoOcasion.CONTRAATAQUE;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  6. xG
    // ══════════════════════════════════════════════════════════════════════

    /**
     * xG = xGBase(tipo) * variación_aleatoria * factorCalidad
     *
     * factorCalidad ∈ [0.75, 1.25] según ataque vs defensa.
     * Resultado acotado a [XG_MIN, XG_MAX].
     */
    private double calcularXG(TipoOcasion tipo, Equipo atacante, Equipo defensor) {
        double xGBase = xGBasePorTipo(tipo);

        double variacion = 1.0 + (random.nextDouble() - 0.5) * 2 * XG_VARIACION;

        double ratio = (double) atacante.getAtaque()
                / (atacante.getAtaque() + defensor.getDefensa());
        double factorCalidad = 0.75 + ratio * 0.5;

        return clamp(xGBase * variacion * factorCalidad, XG_MIN, XG_MAX);
    }

    private double xGBasePorTipo(TipoOcasion tipo) {
        switch (tipo) {
            case CONTRAATAQUE:       return XG_CONTRAATAQUE_BASE;
            case ATAQUE_POSICIONAL:  return XG_ATAQUE_POSICIONAL_BASE;
            case TIRO_LEJANO:        return XG_TIRO_LEJANO_BASE;
            case PELOTA_PARADA:      return XG_PELOTA_PARADA_BASE;
            default:                 return XG_ATAQUE_POSICIONAL_BASE;
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  7. GOL
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Regla fundamental: probabilidad de gol = xG.
     * No se vuelve a ponderar con ataque/defensa (ya está en el xG calculado).
     */
    private boolean esGol(double xG) {
        return random.nextDouble() < xG;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════════════════

    /** Muestra de distribución Poisson (algoritmo de Knuth). */
    private int muestraPoisson(double lambda) {
        if (lambda <= 0) return 0;
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);
        return k - 1;
    }

    private double clamp(double valor, double min, double max) {
        return Math.max(min, Math.min(max, valor));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  RESUMEN CONSOLA
    // ══════════════════════════════════════════════════════════════════════

    private void imprimirResumen(EstadoPartido estado,
                                 double acumPosesionLocal,
                                 List<Evento> eventos) {
        long totalOcasiones = eventos.stream().filter(e -> e instanceof EventoOcasion).count();
        long totalGoles     = eventos.stream().filter(e -> e instanceof EventoGol).count();
        double posL = acumPosesionLocal / MINUTOS_PARTIDO * 100;

        System.out.println("\n=== RESUMEN DEL PARTIDO ===");
        System.out.printf("Posesión  →  %s: %.1f%%  |  %s: %.1f%%%n",
                estado.getLocal().getNombre(), posL,
                estado.getVisitante().getNombre(), 100 - posL);
        System.out.printf("Ocasiones : %d%n", totalOcasiones);
        System.out.printf("Goles     : %d%n", totalGoles);
        System.out.printf("Resultado : %s %d – %d %s%n",
                estado.getLocal().getNombre(), estado.getGolesLocal(),
                estado.getGolesVisitante(), estado.getVisitante().getNombre());
        System.out.println("===========================\n");
    }
}
