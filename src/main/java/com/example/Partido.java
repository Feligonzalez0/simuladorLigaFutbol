package com.example;

public class Partido {
    private Equipo local;
    private Equipo visitante;
    private int golesLocal;
    private int golesVisitante;

    public Partido (Equipo local, Equipo visitante){
        this.local = local;
        this.visitante = visitante;
        this.golesLocal = -1;
        this.golesLocal = -1;
    }

    public void simular(){
        
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
