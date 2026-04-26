package com.example.Simulador.Evento;

import com.example.Simulador.*;

public class EventoOcasion implements Evento {
    private Ocasion ocasion;

    public EventoOcasion(Ocasion ocasion) {
        this.ocasion = ocasion;
    }

    @Override
    public void aplicar(EstadoPartido estado) {
        // Ejemplo simple:
        // aumentar momentum del equipo que atacó
        if (ocasion.getEquipo() == estado.getLocal()) {
            estado.setMomentumLocal(estado.getMomentumLocal() + 0.1);
            estado.setMoralLocal(estado.getMoralLocal() + 0.01);
        } else {
            estado.setMomentumVisitante(estado.getMomentumVisitante() + 0.1);
            estado.setMoralVisitante(estado.getMoralVisitante() + 0.01);
        }
    }

    @Override
    public int getMinuto() {
        return ocasion.getMinuto();
    }

    @Override
    public String getDescripcion() {
        return "Ocasión para " + ocasion.getEquipo().getNombre()
                + " (" + ocasion.getTipo()
                + ", xG= " + String.format("%.2f", ocasion.getXG())
                + ", Minuto: " + ocasion.getMinuto() + ")";
    }

    public Ocasion getOcasion() {
        return ocasion;
    }
}