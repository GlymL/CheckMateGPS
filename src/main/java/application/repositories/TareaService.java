package application.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.entities.Roommate;
import application.entities.Tarea;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private RoommateRepository roommateRepository;

    public Tarea completarTarea(Long tareaId, Long roommateId) {

        Tarea tarea = tareaRepository.findById(tareaId)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (tarea.getCompletada()) {
            throw new RuntimeException("La tarea ya está hecha");
        }

        if (tarea.getAsignadoA() == null) {
            throw new RuntimeException("La tarea no está asignada a ningún roommate");
        }

        Roommate roommate = roommateRepository.findById(roommateId)
            .orElseThrow(() -> new RuntimeException("Roommate no encontrado"));

        tarea.setCompletada(true);
        tarea.setAsignadoA(roommate);

        return tareaRepository.save(tarea);
    }
}