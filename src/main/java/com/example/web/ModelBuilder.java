package com.example.web;

import com.example.*;
import java.util.*;

/**
 * Construye los mapas de datos para los templates Mustache.
 * Traduce objetos del dominio a estructuras Map/List que Mustache puede renderizar.
 */
public class ModelBuilder {

    // ── Modelo para /tabla ────────────────────────────────────────────────
    public static Map<String, Object> tabla(Liga liga) {
        Map<String, Object> model = base(liga);
        List<Map<String, Object>> filas = new ArrayList<>();

        int pos = 1;
        for (RegistroTabla r : liga.getTabla().getRegistros()) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("pos",     pos++);
            fila.put("nombre",  r.getEquipo().getNombre());
            fila.put("pj",      r.getPartidosJugados());
            fila.put("g",       r.getGanados());
            fila.put("e",       r.getEmpatados());
            fila.put("p",       r.getPerdidos());
            fila.put("gf",      r.getGolesFavor());
            fila.put("gc",      r.getGolesContra());
            fila.put("dif",     r.getGolesDif() >= 0
                                    ? "+" + r.getGolesDif()
                                    : String.valueOf(r.getGolesDif()));
            fila.put("pts",     r.getPuntos());
            // destacar top 3
            fila.put("esTop",   pos <= 4);   // ya incrementado, pos-1 <= 3
            filas.add(fila);
        }
        model.put("filas",  filas);
        model.put("fechasJugadas", liga.getFechaActualIdx());
        model.put("totalFechas",   liga.getTotalFechas());
        return model;
    }

    // ── Modelo para /fixture ─────────────────────────────────────────────
    public static Map<String, Object> fixture(Liga liga) {
        Map<String, Object> model = base(liga);
        List<Map<String, Object>> fechasList = new ArrayList<>();

        for (Fecha f : liga.getFechas()) {
            Map<String, Object> fechaMap = new LinkedHashMap<>();
            fechaMap.put("numero",  f.getNumero());
            fechaMap.put("jugada",  f.isJugada());
            fechaMap.put("pendiente", !f.isJugada());

            List<Map<String, Object>> partidosList = new ArrayList<>();
            for (Partido p : f.getPartidos()) {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("local",      p.getLocal().getNombre());
                pm.put("visitante",  p.getVisitante().getNombre());
                pm.put("simulado",   p.isSimulado());
                if (p.isSimulado()) {
                    pm.put("golesLocal",     p.getGolesLocal());
                    pm.put("golesVisitante", p.getGolesVisitante());
                    pm.put("resultado",      p.getGolesLocal() + " - " + p.getGolesVisitante());
                    pm.put("ganóLocal",      p.getGolesLocal() > p.getGolesVisitante());
                    pm.put("empate",         p.getGolesLocal() == p.getGolesVisitante());
                    pm.put("ganóVisitante",  p.getGolesLocal() < p.getGolesVisitante());
                }
                partidosList.add(pm);
            }
            fechaMap.put("partidos", partidosList);
            fechasList.add(fechaMap);
        }
        model.put("fechas", fechasList);
        return model;
    }

    // ── Modelo para / (index) ─────────────────────────────────────────────
    public static Map<String, Object> index(Liga liga, Fecha ultimaFecha) {
        if (liga == null) {
            Map<String, Object> m = new HashMap<>();
            m.put("sinLiga", true);
            return m;
        }

        Map<String, Object> model = base(liga);
        model.put("hayFechasPendientes", liga.hayFechasPendientes());
        model.put("ligaTerminada",       !liga.hayFechasPendientes());
        model.put("proximaFecha",        liga.getFechaActualNumero());
        model.put("totalFechas",   liga.getTotalFechas());
        
        int fechasJugadas = liga.getFechaActualNumero() - 1;
        int totalFechas = liga.getTotalFechas();
        int progreso = (int) ((fechasJugadas * 100.0) / totalFechas);
        model.put("progreso", progreso);
        model.put("fechasJugadas", fechasJugadas);

        // Top 5 de tabla para mostrar en home
        List<Map<String, Object>> top5 = new ArrayList<>();
        List<RegistroTabla> registros = liga.getTabla().getRegistros();
        int limit = Math.min(5, registros.size());
        for (int i = 0; i < limit; i++) {
            RegistroTabla r = registros.get(i);
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("pos",    i + 1);
            fila.put("nombre", r.getEquipo().getNombre());
            fila.put("pts",    r.getPuntos());
            fila.put("pj",     r.getPartidosJugados());
            top5.add(fila);
        }
        model.put("top5", top5);

        // Última fecha simulada
        if (ultimaFecha != null) {
            List<Map<String, Object>> ultPartidos = new ArrayList<>();
            for (Partido p : ultimaFecha.getPartidos()) {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("local",          p.getLocal().getNombre());
                pm.put("visitante",      p.getVisitante().getNombre());
                pm.put("golesLocal",     p.getGolesLocal());
                pm.put("golesVisitante", p.getGolesVisitante());
                pm.put("ganóLocal",      p.getGolesLocal() > p.getGolesVisitante());
                pm.put("empate",         p.getGolesLocal() == p.getGolesVisitante());
                pm.put("ganóVisitante",  p.getGolesLocal() < p.getGolesVisitante());
                ultPartidos.add(pm);
            }
            model.put("ultimaFecha",         ultimaFecha.getNumero());
            model.put("ultimaFechaPartidos", ultPartidos);
            model.put("hayUltimaFecha",      true);
        }

        return model;
    }

    // ── Base común ────────────────────────────────────────────────────────
    private static Map<String, Object> base(Liga liga) {
        Map<String, Object> m = new HashMap<>();
        m.put("ligaNombre",  liga.getNombre());
        m.put("totalEquipos", liga.getEquipos().size());
        return m;
    }
    
}
