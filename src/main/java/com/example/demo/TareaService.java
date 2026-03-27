package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private RoommateRepository roommateRepository;

    public Tarea completarTarea(Long tareaId, Long roommateId) {

        Tarea tarea = tareaRepository.findById(tareaId)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (tarea.isCompletada()) {
            throw new RuntimeException("La tarea ya está hecha");
        }

        if (tarea.getRoommate() == null) {
            throw new RuntimeException("La tarea no está asignada a ningún roommate");
        }

        Roommate roommate = roommateRepository.findById(roommateId)
            .orElseThrow(() -> new RuntimeException("Roommate no encontrado"));

        tarea.setCompletada(true);
        tarea.setCompletedBy(roommate);

        return tareaRepository.save(tarea);
    }
}