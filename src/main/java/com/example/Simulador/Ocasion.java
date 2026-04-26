package com.example.Simulador;

import com.example.*;

public class Ocasion {
    public enum TipoOcasion {
        CONTRAATAQUE,
        ATAQUE_POSICIONAL,
        TIRO_LEJANO,
        PELOTA_PARADA
    }
    
    private Equipo equipo;
    private double xG; // probabilidad base de gol [0.0 - 1.0]
    private TipoOcasion tipo;
    private int minuto;

    public Ocasion(Equipo equipo, double xG, TipoOcasion tipo, int minuto) {
        this.equipo = equipo;
        this.xG = xG;
        this.tipo = tipo;
        this.minuto = minuto;
    }
    
    public Ocasion(Equipo equipo, double xG, int minuto) {
        this.equipo = equipo;
        this.xG = xG;
        this.minuto = minuto;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public double getXG() {
        return xG;
    }

    public TipoOcasion getTipo() {
        return tipo;
    }

    public int getMinuto() {
        return minuto;
    }
}