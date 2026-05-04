package com.example;

import com.example.Simulador.*;
import com.example.Simulador.Evento.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main2 {

    private static final int N_PARTIDOS = 5000;

    public static void main(String[] args) throws Exception {

        SimuladorPartido simulador = new SimuladorPartido();

        // Equipos de prueba (ajustalos a tu DB real)
        Equipo local = new Equipo("Equipo A", 70, 70, 70);
        Equipo visitante = new Equipo("Equipo B", 70, 70, 70);

        List<ResultadoPartido> resultados = new ArrayList<>();

        for (int i = 0; i < N_PARTIDOS; i++) {
            ResultadoPartido r = simularYRecolectar(simulador, local, visitante);
            resultados.add(r);
        }

        generarExcel(resultados);

        System.out.println("Simulación completada. Archivo generado: resultados.xlsx");
    }

    // ─────────────────────────────────────────────

    private static ResultadoPartido simularYRecolectar(SimuladorPartido simulador,
                                                        Equipo local,
                                                        Equipo visitante) {

        EstadoPartido estado = new EstadoPartido(local, visitante);
        List<Evento> eventos = simulador.simular(local, visitante);

        int goles = 0;
        int ocasiones = 0;
        double xG = 0.0;

        for (Evento e : eventos) {
            if (e instanceof EventoGol) {
                goles++;
            } else if (e instanceof EventoOcasion) {
                ocasiones++;
                xG += ((EventoOcasion) e).getOcasion().getXG();
            }
        }

        // posesión promedio (recalcular simple)
        double posesionLocal = simulador.getPosesionLocalFinal();

        return new ResultadoPartido(goles, ocasiones, xG, posesionLocal);
    }

    // ─────────────────────────────────────────────

    private static void generarExcel(List<ResultadoPartido> resultados) throws Exception {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Simulaciones");

        int rowIdx = 0;

        // Header
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Goles");
        header.createCell(1).setCellValue("Ocasiones");
        header.createCell(2).setCellValue("xG");
        header.createCell(3).setCellValue("Posesion Local");

        double sumGoles = 0;
        double sumOcasiones = 0;
        double sumXG = 0;

        // Datos
        for (ResultadoPartido r : resultados) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(r.goles);
            row.createCell(1).setCellValue(r.ocasiones);
            row.createCell(2).setCellValue(r.xg);
            row.createCell(3).setCellValue(r.posesion);

            sumGoles += r.goles;
            sumOcasiones += r.ocasiones;
            sumXG += r.xg;
        }

        int n = resultados.size();

        // Promedios
        Row avgRow = sheet.createRow(rowIdx + 1);

        avgRow.createCell(0).setCellValue("PROMEDIO");
        avgRow.createCell(1).setCellValue(sumGoles / n);
        avgRow.createCell(2).setCellValue(sumOcasiones / n);
        avgRow.createCell(3).setCellValue(sumXG / n);

        // Autosize
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fos = new FileOutputStream("resultados.xlsx");
        wb.write(fos);
        fos.close();
        wb.close();
    }

    // ─────────────────────────────────────────────

    static class ResultadoPartido {
        int goles;
        int ocasiones;
        double xg;
        double posesion;

        ResultadoPartido(int goles, int ocasiones, double xg, double posesion) {
            this.goles = goles;
            this.ocasiones = ocasiones;
            this.xg = xg;
            this.posesion = posesion;
        }
    }
}