package com.example;

import com.example.Simulador.SimuladorPartido;
import com.example.Simulador.Evento.*;
import java.util.List;

public class Partido {
    private Equipo local;
    private Equipo visitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean simulado;

    public Partido(Equipo local, Equipo visitante) {
        this.local         = local;
        this.visitante     = visitante;
        this.golesLocal    = -1;
        this.golesVisitante = -1;
        this.simulado      = false;
    }

    public void simular() {
        SimuladorPartido sim = new SimuladorPartido();
        List<Evento> eventos = sim.simular(local, visitante);
        long gl = eventos.stream()
                .filter(e -> e instanceof EventoGol && ((EventoGol) e).getEquipo() == local)
                .count();
        long gv = eventos.stream()
                .filter(e -> e instanceof EventoGol && ((EventoGol) e).getEquipo() == visitante)
                .count();
        this.golesLocal     = (int) gl;
        this.golesVisitante = (int) gv;
        this.simulado       = true;
    }

    // Getters / setters
    public Equipo getLocal()           { return local; }
    public Equipo getVisitante()       { return visitante; }
    public int    getGolesLocal()      { return golesLocal; }
    public int    getGolesVisitante()  { return golesVisitante; }
    public boolean isSimulado()        { return simulado; }
    public void setGolesLocal(int g)   { this.golesLocal = g; }
    public void setGolesVisitante(int g){ this.golesVisitante = g; }

    public String getResultadoTexto() {
        if (!simulado) return "vs";
        return golesLocal + " - " + golesVisitante;
    }
}
