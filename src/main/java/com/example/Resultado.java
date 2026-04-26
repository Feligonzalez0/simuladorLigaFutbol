package com.example;

public class Resultado {
    private int golesLocal;
    private int golesVisitante;

    // constructor + getters
    public Resultado (int golesL, int golesV){
        this.golesLocal = golesL;
        this.golesVisitante = golesV;
    }

    //Getters
    public int getGolesLocal() {return golesLocal;}
    public int getGolesVisitante() {return golesVisitante;}
}