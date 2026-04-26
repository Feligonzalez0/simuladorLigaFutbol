package com.example;

public class RegistroTabla {
    private Equipo equipo;
    private int puntos;
    private int golesFavor;
    private int golesContra;

    public RegistroTabla(Equipo equipo){
        this.equipo = equipo;
        this.puntos = 0;
        this.golesFavor = 0;
        this.golesContra = 0;
    }

    public int getGolesDif(){
        return this.golesFavor - this.golesContra;
    }

    public void actualizar(Partido partido){
        if (partido.getGolesLocal() == -1 || partido.getGolesVisitante() == -1) throw new IllegalArgumentException("NO se jugó el partido.\n");
        //TODO: implementar actualizar
    }
}
