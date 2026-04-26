package com.example;
import java.util.LinkedList;
import java.util.List;

public class Liga {
    private List<Equipo> equipos;
    private List<Fecha> fechas;
    private Tabla tabla;

    public Liga (List<Equipo> equipos){
        this.equipos = equipos;
        this.fechas = generarFixture(equipos);
        this.tabla = new Tabla(equipos);
    }

    private List<Fecha> generarFixture(List<Equipo> equipos){
        //TODO: implementar generar fixture
        return null;
    }
}
