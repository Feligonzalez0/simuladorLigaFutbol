package com.example;

public class Equipo {
    private String nombre;
    private int ataque; // 1 - 100
    private int medio; // 1 - 100
    private int defensa; // 1 - 100
    private int portero; // 1 - 100
    private float moral; // 0.0 - 2.0
    private float energia; //0.0 - 1.0

    public Equipo (String n, int ata, int mid, int def){
        this.nombre = n;
        this.ataque = ata;
        this.medio = mid;
        this.defensa = def;
        this.moral = 1;
        this.energia = 1;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getMedio() {
        return medio;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getPortero() {
        return portero;
    }

    public float getMoral() {
        return moral;
    }

    public float getEnergia() {
        return energia;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public void setMedio(int medio) {
        this.medio = medio;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public void setPortero(int portero) {
        this.portero = portero;
    }

    public void setMoral(float moral) {
        this.moral = moral;
    }

    public void setEnergia(float energia) {
        this.energia = energia;
    }
}