package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tabla {
    private List<RegistroTabla> registros;

    public Tabla(List<Equipo> equipos) {
        this.registros = new ArrayList<>();
        for (Equipo e : equipos)
            registros.add(new RegistroTabla(e));
    }

    public void actualizar(Partido partido) {
        for (RegistroTabla r : registros)
            r.actualizar(partido);
        ordenarTabla();
    }

    private void ordenarTabla() {
        registros.sort(
            Comparator.comparingInt(RegistroTabla::getPuntos).reversed()
            .thenComparing(Comparator.comparingInt(RegistroTabla::getGolesDif).reversed())
            .thenComparing(Comparator.comparingInt(RegistroTabla::getGolesFavor).reversed())
        );
    }

    public List<RegistroTabla> getRegistros() { return registros; }
}
