package com.example.Simulador.Evento;

import com.example.Simulador.*;

public interface Evento {
    void aplicar(EstadoPartido estado);
    int getMinuto();
    String getDescripcion();
}