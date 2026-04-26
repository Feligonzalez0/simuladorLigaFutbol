package com.example;
import java.util.LinkedList;
import java.util.List;

public class Tabla {
    private List<RegistroTabla> registros;
    
    public Tabla (List<Equipo> equipos){
        this.registros = new LinkedList<>();
        for (Equipo e : equipos){
            RegistroTabla reg = new RegistroTabla(e);
            registros.add(reg);
        }
    }
    
    public void actualizar(Partido partido){
        for (RegistroTabla r : registros){
            r.actualizar(partido);
        }
        
        ordenarTabla();
    }

    private void ordenarTabla(){
        //TODO: Ordenar tabla por; puntos, dif, gf
    }
}
