package application.entities;

import java.time.LocalDate;
import java.util.*;

public class Calendario {

    private Map<LocalDate, List<Tarea>> tareasPorDia;

    public Calendario(List<Tarea> tareas) {
        tareasPorDia = new HashMap<>();
        for (Tarea t : tareas) {
            if (t.getFechaRealizacion() == null) continue;
            LocalDate fecha = t.getFechaRealizacion();
            tareasPorDia.putIfAbsent(fecha, new ArrayList<>());
            tareasPorDia.get(fecha).add(t);
        }
    }

    public List<Tarea> getTareasPorDia(LocalDate fecha) {
        return tareasPorDia.getOrDefault(fecha, new ArrayList<>());
    }

    public Map<LocalDate, List<Tarea>> getAll() {
        return tareasPorDia;
    }
}