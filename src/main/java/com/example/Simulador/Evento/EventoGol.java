package com.example.Simulador.Evento;

import com.example.Equipo;
import com.example.Simulador.EstadoPartido;

public class EventoGol implements Evento {
    private Equipo equipo;
    private int minuto;

    public EventoGol(Equipo equipo, int minuto) {
        this.equipo = equipo;
        this.minuto = minuto;
    }

    @Override
    public void aplicar(EstadoPartido estado) {
        if (equipo == estado.getLocal()) {
            estado.setGolesLocal(estado.getGolesLocal() + 1);
            estado.setMoralLocal(estado.getMoralLocal() + 0.1);
            estado.setMoralVisitante(estado.getMoralVisitante() - 0.1);
            estado.setMomentumVisitante(estado.getMomentumVisitante() - 0.2);
            estado.setMomentumLocal(estado.getMomentumLocal() + 0.3);

        } else {
            estado.setGolesVisitante(estado.getGolesVisitante() + 1);
            estado.setMoralVisitante(estado.getMoralVisitante() + 0.1);
            estado.setMoralLocal(estado.getMoralLocal() - 0.1);
            estado.setMomentumVisitante(estado.getMomentumVisitante() + 0.3);
            estado.setMomentumLocal(estado.getMomentumLocal() - 0.2);
        }
    }

    @Override
    public int getMinuto() {
        return minuto;
    }

    @Override
    public String getDescripcion() {
        return "⚽ Gol de " + equipo.getNombre() + " en el minuto " + minuto;
    }

    public Equipo getEquipo(){return this.equipo;}
}