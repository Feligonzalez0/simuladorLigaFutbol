package com.example;

import java.util.List;

import com.example.Simulador.*;
import com.example.Simulador.Evento.*;
public class Main {
    public static void main(String[] args) {

        List<Equipo> equipos = List.of(
            new Equipo("Equipo A", 70, 65, 60),
            new Equipo("Equipo B", 68, 70, 66),
            new Equipo("Equipo C", 75, 72, 69),
            new Equipo("Equipo D", 65, 60, 64),
            new Equipo("Equipo E", 65, 60, 64)
        );

        Liga liga = new Liga("League Test", equipos);

        //liga.imprimirFixture();
    }
}