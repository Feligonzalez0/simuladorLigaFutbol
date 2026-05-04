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
        registros.sort((a, b) -> {
            // 1. Puntos (desc)
            if (b.getPuntos() != a.getPuntos()) {
                return b.getPuntos() - a.getPuntos();
            }

            // 2. Diferencia de gol (desc)
            if (b.getGolesDif() != a.getGolesDif()) {
                return b.getGolesDif() - a.getGolesDif();
            }

            // 3. Goles a favor (desc)
            if (b.getGolesFavor() != a.getGolesFavor()) {
                return b.getGolesFavor() - a.getGolesFavor();
            }

            return 0;
        });
    }
}
