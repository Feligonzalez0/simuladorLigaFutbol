package com.example;

import java.util.List;

import com.example.Simulador.*;
import com.example.Simulador.Evento.*;
public class Main {
    public static void main(String[] args) {

        // Crear equipos (ajustá según tu clase Equipo)
        Equipo local = new Equipo("Boca", 100, 50, 50);      // ataque, defensa, medio
        Equipo visitante = new Equipo("River", 50, 50, 50);

        // Crear simulador
        SimuladorPartido simulador = new SimuladorPartido();

        // Simular
        List<Evento> eventos = simulador.simular(local, visitante);

        // Mostrar eventos
        System.out.println("=== EVENTOS DEL PARTIDO ===");
        for (Evento e : eventos) {
            System.out.println("Min " + e.getMinuto() + ": " + e.getDescripcion());
        }

        // Calcular resultado final
        int golesLocal = 0;
        int golesVisitante = 0;

        for (Evento e : eventos) {
            if (e instanceof EventoGol) {
                EventoGol gol = (EventoGol) e;

                if (gol.getEquipo() == local) {
                    golesLocal++;
                } else {
                    golesVisitante++;
                }
            }
        }

        // Mostrar resultado
        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println(local.getNombre() + " " + golesLocal +
                           " - " + golesVisitante + " " +
                           visitante.getNombre());
    }
}