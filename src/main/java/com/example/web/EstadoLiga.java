package com.example.web;

import com.example.*;
import java.util.*;

/**
 * Singleton que mantiene el estado de la liga en memoria durante la sesión web.
 */
public class EstadoLiga {

    private static EstadoLiga instancia;

    private Liga liga;
    private Fecha ultimaFechaSimulada;

    private EstadoLiga() {}

    public static synchronized EstadoLiga getInstance() {
        if (instancia == null) instancia = new EstadoLiga();
        return instancia;
    }

    public static synchronized void reset() {
        instancia = new EstadoLiga();
    }

    // ── Inicializar con los equipos por defecto ───────────────────────────
    public void inicializarLigaDefault() {
        List<Equipo> equipos = new ArrayList<>();
        equipos.add(new Equipo("Boca Juniors",   85, 78, 80));
        equipos.add(new Equipo("River Plate",    82, 83, 79));
        equipos.add(new Equipo("Racing Club",    76, 72, 74));
        equipos.add(new Equipo("Independiente",  73, 71, 75));
        equipos.add(new Equipo("San Lorenzo",    74, 70, 72));
        equipos.add(new Equipo("Huracán",        68, 65, 67));
        equipos.add(new Equipo("Vélez",          72, 69, 71));
        equipos.add(new Equipo("Estudiantes",    70, 68, 73));
        equipos.add(new Equipo("Lanús",          69, 66, 70));
        equipos.add(new Equipo("Banfield",       65, 64, 68));
        liga = new Liga("Liga Profesional Argentina", equipos);
        ultimaFechaSimulada = null;
    }

    public void inicializarLiga(String nombre, List<Equipo> equipos) {
        liga = new Liga(nombre, equipos);
        ultimaFechaSimulada = null;
    }

    public Fecha simularSiguiente() {
        if (liga == null || !liga.hayFechasPendientes()) return null;
        ultimaFechaSimulada = liga.simularSiguienteFecha();
        return ultimaFechaSimulada;
    }

    public boolean ligaCreada()           { return liga != null; }
    public Liga getLiga()                 { return liga; }
    public Fecha getUltimaFechaSimulada() { return ultimaFechaSimulada; }
}
