package com.example;

import java.util.List;

public class SimuladorLiga {

    private Liga liga;
    private int fechaActual;
    private boolean finalizada;

    public SimuladorLiga(String nombre, List<Equipo> equipos){
        this.liga = new Liga(nombre, equipos);
        this.fechaActual = 0;
        this.finalizada = false;
    }

    public void simularSiguienteFecha(){
        if (finalizada) {
            throw new IllegalStateException("La liga ya finalizó.");
        }

        List<Fecha> fechas = liga.getFixture();

        if (fechaActual >= fechas.size()){
            finalizada = true;
            return;
        }

        Fecha fecha = fechas.get(fechaActual);

        fecha.jugarFecha(getTabla());

        fechaActual++;

        if (fechaActual >= fechas.size()){
            finalizada = true;
        }
    }

    public boolean isFinalizada(){
        return finalizada;
    }

    public int getFechaActual(){
        return fechaActual;
    }

    public Tabla getTabla(){
        return liga.getTabla();
    }

    public List<Fecha> getFixture(){
        return liga.getFixture();
    }
}
