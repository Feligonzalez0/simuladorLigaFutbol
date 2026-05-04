package com.example;

import java.util.List;

public class Fecha {
    private int numero;
    private List<Partido> partidos;
    private boolean jugada;

    public Fecha(int numero, List<Partido> partidos) {
        this.numero   = numero;
        this.partidos = partidos;
        this.jugada   = false;
    }

    public void jugarFecha(Tabla tabla) {
        for (Partido p : partidos) {
            p.simular();
            tabla.actualizar(p);
        }
        this.jugada = true;
    }

    public int          getNumero()   { return numero; }
    public List<Partido> getPartidos() { return partidos; }
    public boolean      isJugada()    { return jugada; }
}
