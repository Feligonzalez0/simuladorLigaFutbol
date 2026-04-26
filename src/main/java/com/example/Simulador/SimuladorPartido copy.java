package com.example.Simulador;

import com.example.*;
import com.example.Simulador.Ocasion.TipoOcasion;
import com.example.Simulador.Evento.*;

import java.util.*;

public class SimuladorPartido {

    private Random random = new Random();

    public List<Evento> simular(Equipo local, Equipo visitante) {
        EstadoPartido estado = new EstadoPartido(local, visitante);
        List<Evento> eventos = new ArrayList<>();
        double sumaPosesionLocal = 0.0;
        for (int minuto = 1; minuto <= 90; minuto++) {
            estado.setMinuto(minuto);

            // 1. actualizar estado general
            actualizarEnergia(estado);
            actualizarPosesion(estado);
            actualizarMomentum(estado);

            System.out.println("Min " + estado.getMinuto() +
            " | Pos L: " + String.format("%.2f", estado.getPosesionLocal()) +
            " | Mom L: " + String.format("%.2f", estado.getMomentumLocal()) +
            " | Mom V: " + String.format("%.2f", estado.getMomentumVisitante()));
            // 2. intentar generar ocasión
            Optional<Ocasion> ocasion = generarOcasion(estado);

            if (ocasion.isPresent()) {
                Evento eventoOcasion = new EventoOcasion(ocasion.get());
                eventoOcasion.aplicar(estado);
                eventos.add(eventoOcasion);

                // 3. resolver si es gol
                if (esGol(ocasion.get(), estado)) {
                    Evento gol = new EventoGol(ocasion.get().getEquipo(), minuto);
                    gol.aplicar(estado);
                    eventos.add(gol);
                }
            }
            sumaPosesionLocal += estado.getPosesionLocal();
            
        }
        double promedioPosesionLocal = sumaPosesionLocal / 90;
        double promedioPosesionVisitante = 1 - promedioPosesionLocal;
        System.out.println("Posesión promedio:");
        System.out.println("Local: " + String.format("%.1f%%", promedioPosesionLocal * 100));
        System.out.println("Visitante: " + String.format("%.1f%%", promedioPosesionVisitante * 100));
        return eventos;
    }

    private void actualizarPosesion(EstadoPartido estado) {
        Equipo local = estado.getLocal();
        Equipo visitante = estado.getVisitante();

        double base = (double) local.getMedio() / (local.getMedio() + visitante.getMedio());

        double softer = 0.3;

        double ajusteLocal = 1 + (estado.getMoralLocal() - 1) * softer
                            + (estado.getEnergiaLocal() - 1) * softer;

        double ajusteVisit = 1 + (estado.getMoralVisitante() - 1) * softer
                            + (estado.getEnergiaVisitante() - 1) * softer;

        double ruidoLocal = 1 + (random.nextDouble() - 0.5) * 0.3;
        double ruidoVisit = 1 + (random.nextDouble() - 0.5) * 0.3;

        double fuerzaLocal = Math.pow(local.getMedio(), 0.8) * ajusteLocal * ruidoLocal;
        double fuerzaVisit = Math.pow(visitante.getMedio(), 0.8) * ajusteVisit * ruidoVisit;

        double posesionAjustada = fuerzaLocal / (fuerzaLocal + fuerzaVisit);

        double impactoMomentum = (estado.getMomentumLocal() - estado.getMomentumVisitante()) * 0.03;
        posesionAjustada += impactoMomentum;

        double posesionObjetivo = 0.6 * base + 0.4 * posesionAjustada;

        double actual = estado.getPosesionLocal();
        double alpha = 0.10;
        double nueva = actual + alpha * (posesionObjetivo - actual);

        double ruido = (random.nextDouble() - 0.5) * 0.25;
        nueva += ruido;

        double retorno = (base - nueva) * 0.04;
        nueva += retorno;

        estado.setPosesionLocal(nueva);
    }
    private void actualizarEnergia(EstadoPartido estado) {
        //TODO: Actualizar energia de forma dinamica segun estado del partido
        estado.setEnergiaLocal(estado.getEnergiaLocal() - 0.005);
        estado.setEnergiaVisitante(estado.getEnergiaVisitante() - 0.005);
    }

    private Optional<Ocasion> generarOcasion(EstadoPartido estado) {
        double probBase = 0.15; // ajustar

        // más posesión = más chances
        double probLocal = probBase * estado.getPosesionLocal();
        double probVisit = probBase * (1 - estado.getPosesionLocal());

        double r = random.nextDouble();

        if (r < probBase) {
            // hay ocasión

            double r2 = random.nextDouble();

            if (r2 < estado.getPosesionLocal()) {
                return Optional.of(crearOcasion(estado.getLocal(), estado));
            } else {
                return Optional.of(crearOcasion(estado.getVisitante(), estado));
            }
        }
        return Optional.empty();
    }

    private Ocasion crearOcasion(Equipo equipo, EstadoPartido estado) {
        TipoOcasion tipo = TipoOcasion.values()[random.nextInt(TipoOcasion.values().length)];
        // TODO: Cada tipo de ocasion debe tener distintas chances de aparecer

        double xGBase;

        switch (tipo) {
            case CONTRAATAQUE: xGBase = 0.3; break;
            case ATAQUE_POSICIONAL: xGBase = 0.2; break;
            case TIRO_LEJANO: xGBase = 0.05; break;
            case PELOTA_PARADA: xGBase = 0.25; break;
            default: xGBase = 0.1;
        }

        // pequeño ruido
        double xG = xGBase * (0.8 + random.nextDouble() * 0.4);

        return new Ocasion(equipo, xG, tipo, estado.getMinuto());
    }

    private boolean esGol(Ocasion ocasion, EstadoPartido estado) {
        Equipo atacante = ocasion.getEquipo();
        Equipo defensor = estado.esLocal(atacante) ? estado.getVisitante() : estado.getLocal();

        double ataque = atacante.getAtaque();
        double defensa = defensor.getDefensa();
        // TODO ¿Se podria mejorar?
        double factor = ataque / (ataque + defensa);

        double probGol = ocasion.getXG() * factor;

        return random.nextDouble() < probGol;
    }

    private void actualizarMomentum(EstadoPartido estado) {
        estado.setMomentumLocal(decayMomentum(estado.getMomentumLocal()));
        estado.setMomentumVisitante(decayMomentum(estado.getMomentumVisitante()));
    }

    private double decayMomentum(double m) {
        double baseDecay = 0.90;     // decaimiento mínimo
        double extraDecay = 0.25;    // cuánto más decae si es extremo

        double factor = baseDecay - (extraDecay * Math.abs(m));
        // cuanto mayor |m|, menor factor → más rápido cae

        return m * factor;
    }
}