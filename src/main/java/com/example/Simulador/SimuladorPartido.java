package com.example.Simulador;

import com.example.*;
import com.example.Simulador.Evento.*;

import java.util.*;

public class SimuladorPartido {

    private Random random = new Random();

    public List<Evento> simular(Equipo local, Equipo visitante) {
        EstadoPartido estado = new EstadoPartido(local, visitante);
        List<Evento> eventos = new ArrayList<>();

        for (int minuto = 1; minuto <= 90; minuto++) {
            estado.setMinuto(minuto);

            actualizarEnergia(estado);
            actualizarMomentum(estado);

            double posesionLocal = calcularPosesion(estado);
            estado.setPosesionLocal(posesionLocal);

            // 1. generar ataques (más realista que ocasiones directas)
            int ataquesLocal = generarAtaques(local, visitante, posesionLocal);
            int ataquesVisit = generarAtaques(visitante, local, 1 - posesionLocal);

            // 2. convertir ataques en ocasiones
            procesarAtaques(estado, local, visitante, ataquesLocal, eventos);
            procesarAtaques(estado, visitante, local, ataquesVisit, eventos);
        }

        return eventos;
    }

    // ---------------- POSESIÓN ----------------

    private double calcularPosesion(EstadoPartido e) {
        Equipo l = e.getLocal();
        Equipo v = e.getVisitante();

        double base = (double) l.getMedio() / (l.getMedio() + v.getMedio());

        double ajuste =
                (e.getMomentumLocal() - e.getMomentumVisitante()) * 0.1 +
                (e.getEnergiaLocal() - e.getEnergiaVisitante()) * 0.05;

        double ruido = (random.nextDouble() - 0.5) * 0.1;

        double posesion = base + ajuste + ruido;

        return clamp(posesion, 0.2, 0.8);
    }

    // ---------------- ATAQUES ----------------

    private int generarAtaques(Equipo atk, Equipo def, double posesion) {
        double intensidad = atk.getMedio() * posesion;

        // ataques por minuto (Poisson simplificado)
        double lambda = intensidad / 120.0;

        return poisson(lambda);
    }

    // ---------------- OCASIONES ----------------

    private void procesarAtaques(EstadoPartido estado,
                                Equipo atacante,
                                Equipo defensor,
                                int ataques,
                                List<Evento> eventos) {

        for (int i = 0; i < ataques; i++) {

            double probOcasion = 0.35 * (atacante.getAtaque() / 
                                (double)(atacante.getAtaque() + defensor.getDefensa()));

            if (random.nextDouble() < probOcasion) {

                double xG = calcularXG(atacante, defensor);

                EventoOcasion ev = new EventoOcasion(
                        new Ocasion(atacante, xG, estado.getMinuto())
                );

                ev.aplicar(estado);
                eventos.add(ev);

                // 4. resolver gol
                if (esGol(xG, atacante, defensor)) {
                    EventoGol gol = new EventoGol(atacante, estado.getMinuto());
                    gol.aplicar(estado);
                    eventos.add(gol);

                    // impacto real en momentum
                    if (estado.esLocal(atacante)) {
                        estado.setMomentumLocal(estado.getMomentumLocal() + 0.3);
                        estado.setMomentumVisitante(estado.getMomentumVisitante() - 0.3);
                    } else {
                        estado.setMomentumVisitante(estado.getMomentumVisitante() + 0.3);
                        estado.setMomentumLocal(estado.getMomentumLocal() - 0.3);
                    }
                }
            }
        }
    }

    private double calcularXG(Equipo atk, Equipo def) {
        double calidad = atk.getAtaque() / (double)(atk.getAtaque() + def.getDefensa());

        double base = 0.1 + 0.4 * calidad;

        double ruido = 0.8 + random.nextDouble() * 0.4;

        return base * ruido;
    }

    private boolean esGol(double xG, Equipo atk, Equipo def) {
        double calidad = atk.getAtaque() / (double)(atk.getAtaque() + def.getDefensa());

        double prob = xG * (0.8 + 0.4 * calidad);

        return random.nextDouble() < prob;
    }

    // ---------------- ENERGÍA ----------------

    private void actualizarEnergia(EstadoPartido e) {
        e.setEnergiaLocal(e.getEnergiaLocal() - 0.004);
        e.setEnergiaVisitante(e.getEnergiaVisitante() - 0.004);
    }

    // ---------------- MOMENTUM ----------------

    private void actualizarMomentum(EstadoPartido e) {
        e.setMomentumLocal(decay(e.getMomentumLocal()));
        e.setMomentumVisitante(decay(e.getMomentumVisitante()));
    }

    private double decay(double m) {
        return m * 0.92;
    }

    // ---------------- UTILS ----------------

    private int poisson(double lambda) {
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}