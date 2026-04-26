package com.example;
import java.util.List;

public class Fecha {
    private List<Partido> partidos;

    public Fecha(List<Partido> partidos){
        this.partidos = partidos;
    }

    public void jugarFecha(Tabla tabla) {
        for (Partido p : partidos) {
            p.simular();
            tabla.actualizar(p);
        }
    }
}
