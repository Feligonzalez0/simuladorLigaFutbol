package DB;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

class StatsEquipo {
    String nombre;
    double g, t, rd, p, pp, rm, gc, i, rdef;

    public StatsEquipo(String nombre, double g, double t, double rd,
                       double p, double pp, double rm,
                       double gc, double i, double rdef) {
        this.nombre = nombre;
        this.g = g;
        this.t = t;
        this.rd = rd;
        this.p = p;
        this.pp = pp;
        this.rm = rm;
        this.gc = gc;
        this.i = i;
        this.rdef = rdef;
    }
}

class EquipoFinal {
    String nombre;
    int ataque, medio, defensa;

    public EquipoFinal(String n, int a, int m, int d) {
        nombre = n; ataque = a; medio = m; defensa = d;
    }
}

public class ExcelDBGenerator {

    public static void main(String[] args) throws Exception {
        String input = "equipos.xlsx";
        String output = "/output/equipos_final.xlsx";

        List<StatsEquipo> datos = leerExcel(input);
        List<EquipoFinal> resultado = generarDB(datos);
        escribirExcel(output, resultado);

        System.out.println("Excel generado correctamente.");
    }

    // ================== LECTURA ==================
    public static List<StatsEquipo> leerExcel(String path) throws Exception {
        List<StatsEquipo> lista = new ArrayList<>();

        FileInputStream fis = new FileInputStream(path);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheet("Datos");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);

            lista.add(new StatsEquipo(
                r.getCell(0).getStringCellValue(),
                r.getCell(1).getNumericCellValue(),
                r.getCell(2).getNumericCellValue(),
                r.getCell(3).getNumericCellValue(),
                r.getCell(4).getNumericCellValue(),
                r.getCell(5).getNumericCellValue(),
                r.getCell(6).getNumericCellValue(),
                r.getCell(7).getNumericCellValue(),
                r.getCell(8).getNumericCellValue(),
                r.getCell(9).getNumericCellValue()
            ));
        }

        wb.close();
        return lista;
    }

    // ================== PROCESO ==================
    public static List<EquipoFinal> generarDB(List<StatsEquipo> equipos) {

        List<Double> A = new ArrayList<>();
        List<Double> M = new ArrayList<>();
        List<Double> D = new ArrayList<>();

        for (StatsEquipo e : equipos) {
            A.add(e.g * 0.5 + e.t * 0.2 + e.rd * 0.3);
            M.add(e.p * 0.4 + e.pp * 0.2 + e.rm * 0.4);
            D.add((1.0 / e.gc) * 0.5 + e.i * 0.2 + e.rdef * 0.3);
        }

        double minA = Collections.min(A), maxA = Collections.max(A);
        double minM = Collections.min(M), maxM = Collections.max(M);
        double minD = Collections.min(D), maxD = Collections.max(D);

        List<EquipoFinal> res = new ArrayList<>();

        for (int i = 0; i < equipos.size(); i++) {
            int ataque = clamp(norm(A.get(i), minA, maxA));
            int medio = clamp(norm(M.get(i), minM, maxM));
            int defensa = clamp(norm(D.get(i), minD, maxD));

            res.add(new EquipoFinal(equipos.get(i).nombre, ataque, medio, defensa));
        }

        return res;
    }

    private static int norm(double v, double min, double max) {
        if (max == min) return 75;
        return (int) Math.round(50 + 50 * (v - min) / (max - min));
    }

    private static int clamp(int v) {
        return Math.max(65, Math.min(90, v));
    }

    // ================== ESCRITURA ==================
    public static void escribirExcel(String path, List<EquipoFinal> equipos) throws Exception {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Resultados");

        // header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("nombre");
        header.createCell(1).setCellValue("ataque");
        header.createCell(2).setCellValue("medio");
        header.createCell(3).setCellValue("defensa");

        int rowNum = 1;
        for (EquipoFinal e : equipos) {
            Row r = sheet.createRow(rowNum++);
            r.createCell(0).setCellValue(e.nombre);
            r.createCell(1).setCellValue(e.ataque);
            r.createCell(2).setCellValue(e.medio);
            r.createCell(3).setCellValue(e.defensa);
        }

        FileOutputStream fos = new FileOutputStream(path);
        wb.write(fos);
        wb.close();
    }
}