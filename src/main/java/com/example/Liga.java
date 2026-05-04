package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Liga {
    private String nombre;
    private List<Equipo> equipos;
    private List<Fecha> fechas;
    private Tabla tabla;
    private int fechaActual; // índice de la próxima fecha a jugar (0-based)

    public Liga(String nombre, List<Equipo> equipos) {
        this.nombre    = nombre;
        this.equipos   = equipos;
        this.fechas    = generarFixture(equipos);
        this.tabla     = new Tabla(equipos);
        this.fechaActual = 0;
    }

    // ── Fixture round-robin (algoritmo de rotación) ──────────────────────
    private List<Fecha> generarFixture(List<Equipo> equipos) {
        List<Fecha> fechas = new LinkedList<>();
        List<Equipo> lista = new LinkedList<>(equipos);
        
        Collections.shuffle(lista); // aleatoriedad

        if (lista.size() % 2 != 0) {
            lista.add(null);
        }

        int n = lista.size();
        int totalFechas = n - 1;

        // contador de localías por equipo
        Map<Equipo, Integer> localias = new HashMap<>();
        for (Equipo e : lista) {
            if (e != null) localias.put(e, 0);
        }

        int maxLocal = totalFechas / 2;

        for (int f = 0; f < totalFechas; f++) {
            List<Partido> partidos = new LinkedList<>();

            for (int i = 0; i < n / 2; i++) {
                Equipo e1 = lista.get(i);
                Equipo e2 = lista.get(n - 1 - i);

                if (e1 != null && e2 != null) {
                    Equipo local;
                    Equipo visitante;

                    int l1 = localias.get(e1);
                    int l2 = localias.get(e2);

                    // regla principal: balancear cantidad de localías
                    if (l1 < l2 && l1 < maxLocal) {
                        local = e1;
                        visitante = e2;
                    } else if (l2 < l1 && l2 < maxLocal) {
                        local = e2;
                        visitante = e1;
                    } else {
                        // fallback: alternancia por fecha
                        if ((f + i) % 2 == 0) {
                            local = e1;
                            visitante = e2;
                        } else {
                            local = e2;
                            visitante = e1;
                        }
                    }

                    localias.put(local, localias.get(local) + 1);

                    partidos.add(new Partido(local, visitante));
                }
            }

            fechas.add(new Fecha(f+1, partidos));

            // rotación
            Equipo ultimo = lista.remove(n - 1);
            lista.add(1, ultimo);
        }

        return fechas;
    }

    // ── Simular la siguiente fecha pendiente ─────────────────────────────
    public Fecha simularSiguienteFecha() {
        if (fechaActual >= fechas.size()) return null;
        Fecha f = fechas.get(fechaActual);
        f.jugarFecha(tabla);
        fechaActual++;
        return f;
    }

    public boolean hayFechasPendientes() { return fechaActual < fechas.size(); }
    public int  getFechaActualNumero()   { return fechaActual + 1; }
    public int  getTotalFechas()         { return fechas.size(); }
    public String getNombre()            { return nombre; }
    public List<Equipo> getEquipos()     { return equipos; }
    public List<Fecha>  getFechas()      { return fechas; }
    public Tabla        getTabla()       { return tabla; }
    public int          getFechaActualIdx() { return fechaActual; }
}
