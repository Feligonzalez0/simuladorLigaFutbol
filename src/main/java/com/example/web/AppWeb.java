package com.example.web;

import com.example.*;
import com.github.mustachejava.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.*;

import static spark.Spark.*;
/* 
# 1. Compilar y empaquetar:
mvn clean package -q

# 2. Ejecutar:
java -jar target/simulador-liga-web-1.0-SNAPSHOT.jar

# 3. Abrir el navegador:
#    http://localhost:4567 */
/**
 * Punto de entrada de la aplicación web.
 * Levanta Spark en el puerto 4567 y define todas las rutas.
 */
public class AppWeb {

    private static final MustacheTemplateEngine TEMPLATE_ENGINE = new MustacheTemplateEngine();

    public static void main(String[] args) {

        // ── Configuración Spark ────────────────────────────────────────────
        port(4567);
        staticFiles.location("/static");

        // Inicializar liga con equipos por defecto al arrancar
        EstadoLiga.getInstance().inicializarLigaDefault();

        // ── GET / ──────────────────────────────────────────────────────────
        get("/", (req, res) -> {
            EstadoLiga estado = EstadoLiga.getInstance();
            Map<String, Object> model = ModelBuilder.index(
                    estado.getLiga(),
                    estado.getUltimaFechaSimulada());
            return new ModelAndView(model, "index.mustache");
        }, TEMPLATE_ENGINE);

        // ── GET /tabla ─────────────────────────────────────────────────────
        get("/tabla", (req, res) -> {
            EstadoLiga estado = EstadoLiga.getInstance();
            if (!estado.ligaCreada()) {
                res.redirect("/");
                return null;
            }
            Map<String, Object> model = ModelBuilder.tabla(estado.getLiga());
            return new ModelAndView(model, "tabla.mustache");
        }, TEMPLATE_ENGINE);

        // ── GET /fixture ───────────────────────────────────────────────────
        get("/fixture", (req, res) -> {
            EstadoLiga estado = EstadoLiga.getInstance();
            if (!estado.ligaCreada()) {
                res.redirect("/");
                return null;
            }
            Map<String, Object> model = ModelBuilder.fixture(estado.getLiga());
            return new ModelAndView(model, "fixture.mustache");
        }, TEMPLATE_ENGINE);

        // ── POST /simular ──────────────────────────────────────────────────
        post("/simular", (req, res) -> {
            EstadoLiga estado = EstadoLiga.getInstance();
            if (estado.ligaCreada()) {
                estado.simularSiguiente();
            }
            res.redirect("/");
            return null;
        });

        // ── POST /nueva-liga ───────────────────────────────────────────────
        post("/nueva-liga", (req, res) -> {
            EstadoLiga.reset();
            EstadoLiga.getInstance().inicializarLigaDefault();
            res.redirect("/");
            return null;
        });

        // ── Confirmar inicio ───────────────────────────────────────────────
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Simulador Liga  →  http://localhost:4567");
        System.out.println("═══════════════════════════════════════");
    }
}
