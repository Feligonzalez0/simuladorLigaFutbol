package com.example.Simulador;

import com.example.*;

public class EstadoPartido {

    private Equipo local;
    private Equipo visitante;

    private int minuto;

    private int golesLocal;
    private int golesVisitante;

    private double posesionLocal;     // [0.0 - 1.0]
    private double posesionVisitante; // derivado o complementario

    private double moralLocal;        // [0.0 - 1.0]
    private double moralVisitante;

    private double energiaLocal;      // [0.0 - 1.0]
    private double energiaVisitante;

    private double momentumLocal;     // [-1.0 - 1.0]
    private double momentumVisitante;

    public EstadoPartido(Equipo local, Equipo visitante) {
        this.local = local;
        this.visitante = visitante;

        this.minuto = 0;

        this.golesLocal = 0;
        this.golesVisitante = 0;

        this.posesionLocal = 0.5;
        this.posesionVisitante = 0.5;

        this.moralLocal = 1.0;
        this.moralVisitante = 1.0;

        this.energiaLocal = 1.0;
        this.energiaVisitante = 1.0;

        this.momentumLocal = 0.0;
        this.momentumVisitante = 0.0;
    }

    // --- getters ---

    public Equipo getLocal() {
        return local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public int getMinuto() {
        return minuto;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public double getPosesionLocal() {
        return posesionLocal;
    }

    public double getPosesionVisitante() {
        return posesionVisitante;
    }

    public double getMoralLocal() {
        return moralLocal;
    }

    public double getMoralVisitante() {
        return moralVisitante;
    }

    public double getEnergiaLocal() {
        return energiaLocal;
    }

    public double getEnergiaVisitante() {
        return energiaVisitante;
    }

    public double getMomentumLocal() {
        return momentumLocal;
    }

    public double getMomentumVisitante() {
        return momentumVisitante;
    }

    // --- setters controlados (importante limitar rangos) ---

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public void setPosesionLocal(double posesionLocal) {
        this.posesionLocal = clamp(posesionLocal, 0.0, 1.0);
        this.posesionVisitante = 1.0 - this.posesionLocal;
    }

    public void setMoralLocal(double moralLocal) {
        this.moralLocal = clamp(moralLocal, 0.0, 1.0);
    }

    public void setMoralVisitante(double moralVisitante) {
        this.moralVisitante = clamp(moralVisitante, 0.0, 1.0);
    }

    public void setEnergiaLocal(double energiaLocal) {
        this.energiaLocal = clamp(energiaLocal, 0.0, 1.0);
    }

    public void setEnergiaVisitante(double energiaVisitante) {
        this.energiaVisitante = clamp(energiaVisitante, 0.0, 1.0);
    }

    public void setMomentumLocal(double momentumLocal) {
        this.momentumLocal = clamp(momentumLocal, -1.0, 1.0);
    }

    public void setMomentumVisitante(double momentumVisitante) {
        this.momentumVisitante = clamp(momentumVisitante, -1.0, 1.0);
    }

    // --- helpers útiles ---

    public boolean esLocal(Equipo equipo) {
        return equipo == local;
    }

    public void sumarGol(Equipo equipo) {
        if (esLocal(equipo)) {
            golesLocal++;
        } else {
            golesVisitante++;
        }
    }

    public int diferenciaGoles() {
        return golesLocal - golesVisitante;
    }

    // --- utilidad interna ---

    private double clamp(double valor, double min, double max) {
        return Math.max(min, Math.min(max, valor));
    }
}