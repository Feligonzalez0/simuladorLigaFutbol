package com.example;

public class RegistroTabla {
    private Equipo equipo;
    private int puntos;
    private int golesFavor;
    private int golesContra;
    private int pJ;
    private int pG;
    private int pE;
    private int pP;

    public RegistroTabla(Equipo equipo){
        this.equipo = equipo;
        this.puntos = 0;
        this.golesFavor = 0;
        this.golesContra = 0;
        this.pJ = 0;
        this.pG = 0;
        this.pE = 0;
        this.pP = 0;
    }

    public void actualizar(Partido partido){
        if (partido.getGolesLocal() == -1 || partido.getGolesVisitante() == -1) 
            throw new IllegalArgumentException("NO se jugó el partido.\n");
        
        int resultado = determinarGanador(partido);

        if (equipo.equals(partido.getLocal())){
            pJ++;
            golesFavor += partido.getGolesLocal();
            golesContra += partido.getGolesVisitante();

            if (resultado == 1){ // LOCAL GANÓ
                pG++;
                puntos += 3;
            } else if (resultado == -1){ // LOCAL PERDIÓ
                pP++;
            } else{ // EMPATE
                pE++;
                puntos++;
            }
        } else if (equipo.equals(partido.getVisitante())){
            pJ++;
            golesFavor += partido.getGolesVisitante();
            golesContra += partido.getGolesLocal();

            if (resultado == -1){ // VISITA GANÓ
                pG++;
                puntos += 3;
            } else if (resultado == 1){ // VISITA PERDIÓ
                pP++;
            } else{ // EMPATE
                pE++;
                puntos++;
            }
        }
    }

    /**
     * Determina el resultado del partido.
     *
     * @param partido Partido ya jugado (con goles válidos)
     * @return 1 si gana el local, -1 si gana el visitante, 0 si hay empate
    */
    private int determinarGanador(Partido partido){
        int golesLocal = partido.getGolesLocal();
        int golesVisita = partido.getGolesVisitante();
        int dif = golesLocal - golesVisita;
        if (dif > 0){
            return 1;
        } else if (dif < 0){
            return -1;
        } else {
            return 0;
        }
    }

    public boolean repOK(){
        if (puntos != (pG * 3) + pE) return false;
        if (pJ != pG + pE + pP) return false;
        if (golesContra < 0 || golesFavor < 0) return false;
        if (pJ < 0 || pG < 0 || pE < 0 || pP < 0) return false;
        if (pG > pJ || pE > pJ || pP > pJ) return false;

        return true;
    }

    public int getPuntos() { return puntos; }
    public int getGolesFavor() { return golesFavor; }
    public int getGolesDif() { return golesFavor - golesContra; }
}
