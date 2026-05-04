package com.example;

import java.util.List;

import com.example.Simulador.SimuladorPartido;
import com.example.Simulador.Evento.Evento;
import com.example.Simulador.Evento.EventoGol;
import com.example.Simulador.Evento.EventoOcasion;

public class Partido {
    private Equipo local;
    private Equipo visitante;
    private int golesLocal;
    private int golesVisitante;
    private List<Evento> eventos;

    public Partido (Equipo local, Equipo visitante){
        this.local = local;
        this.visitante = visitante;
        this.golesLocal = -1;
        this.golesLocal = -1;
    }

    public void simular(){
        SimuladorPartido simulador = new SimuladorPartido();
        
        List<Evento> eventosSimulados = simulador.simular(local, visitante);

        this.eventos = eventosSimulados;

        int golesLocal = 0;
        int golesVisitante = 0;

        for (Evento e : eventosSimulados){
            if (e instanceof EventoGol) {
                if (((EventoGol)e).getEquipo().equals(local)){
                    golesLocal++;
                } else if (((EventoGol)e).getEquipo().equals(visitante)){
                    golesVisitante++;
                }
            } 
        }
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }
    
    // Getters y setters
    public Equipo getLocal() {
        return local;
    }

    public void setLocal(Equipo local) {
        this.local = local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public void setVisitante(Equipo visitante) {
        this.visitante = visitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }
}
