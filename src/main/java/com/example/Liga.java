package com.example;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Liga {
    private String nombre;
    private List<Equipo> equipos;
    private List<Fecha> fixture;
    private Tabla tabla;

    public Liga (String nombre, List<Equipo> equipos){
        this.nombre = nombre;
        this.equipos = equipos;
        this.fixture = generarFixture(equipos);
        this.tabla = new Tabla(equipos);
    }

    private List<Fecha> generarFixture(List<Equipo> equipos) {
        List<Fecha> fechas = new LinkedList<>();
        List<Equipo> lista = new LinkedList<>(equipos);
        
        if (lista.size() % 2 != 0) {
            lista.add(null);
        }

        int n = lista.size();
        int totalFechas = n - 1;

        // contador de localías por equipo
        Map<Equipo, Integer> localias = new HashMap<>();
        for (Equipo e : lista) {
            if (e != null) localias.put(e, 0);
        }

        int maxLocal = totalFechas / 2;

        for (int f = 0; f < totalFechas; f++) {
            List<Partido> partidos = new LinkedList<>();

            for (int i = 0; i < n / 2; i++) {
                Equipo e1 = lista.get(i);
                Equipo e2 = lista.get(n - 1 - i);

                if (e1 != null && e2 != null) {
                    Equipo local;
                    Equipo visitante;

                    int l1 = localias.get(e1);
                    int l2 = localias.get(e2);

                    // regla principal: balancear cantidad de localías
                    if (l1 < l2 && l1 < maxLocal) {
                        local = e1;
                        visitante = e2;
                    } else if (l2 < l1 && l2 < maxLocal) {
                        local = e2;
                        visitante = e1;
                    } else {
                        // fallback: alternancia por fecha
                        if ((f + i) % 2 == 0) {
                            local = e1;
                            visitante = e2;
                        } else {
                            local = e2;
                            visitante = e1;
                        }
                    }

                    localias.put(local, localias.get(local) + 1);

                    partidos.add(new Partido(local, visitante));
                }
            }

            fechas.add(new Fecha(partidos));

            // rotación
            Equipo ultimo = lista.remove(n - 1);
            lista.add(1, ultimo);
        }

        return fechas;
    }

    void imprimirFixture(){
        System.err.println("--- FIXTURE " + this.nombre + " ---");

        for (int i = 0; i < fixture.size(); i++){
            System.err.println("FECHA " + (i + 1));
            
            Fecha fecha = fixture.get(i);
            List<Partido> partidos = fecha.getPartidos();
            
            for(int j = 0; j < partidos.size(); j++){
                Partido partido = partidos.get(j);
                String local = partido.getLocal().getNombre();
                String visita = partido.getVisitante().getNombre();
                System.err.println(local + " VS " + visita);
            }
            System.err.println();
        }
    }

    public List<Fecha> getFixture() {
        return fixture;
    }

    public Tabla getTabla() {
        return tabla;
    }
}
