package com.example;

public class RegistroTabla {
    private Equipo equipo;
    private int puntos;
    private int partidosJugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private int golesFavor;
    private int golesContra;

    public RegistroTabla(Equipo equipo) {
        this.equipo = equipo;
    }

    public void actualizar(Partido partido) {
        if (partido.getGolesLocal() == -1 || partido.getGolesVisitante() == -1)
            throw new IllegalArgumentException("El partido no fue simulado.");

        boolean esLocal     = partido.getLocal()     == equipo;
        boolean esVisitante = partido.getVisitante() == equipo;
        if (!esLocal && !esVisitante) return;

        int gf = esLocal ? partido.getGolesLocal()    : partido.getGolesVisitante();
        int gc = esLocal ? partido.getGolesVisitante(): partido.getGolesLocal();

        partidosJugados++;
        golesFavor  += gf;
        golesContra += gc;

        if      (gf > gc) { ganados++;   puntos += 3; }
        else if (gf == gc){ empatados++; puntos += 1; }
        else               { perdidos++; }
    }

    public int getGolesDif()       { return golesFavor - golesContra; }
    public Equipo getEquipo()      { return equipo; }
    public int getPuntos()         { return puntos; }
    public int getPartidosJugados(){ return partidosJugados; }
    public int getGanados()        { return ganados; }
    public int getEmpatados()      { return empatados; }
    public int getPerdidos()       { return perdidos; }
    public int getGolesFavor()     { return golesFavor; }
    public int getGolesContra()    { return golesContra; }
}
