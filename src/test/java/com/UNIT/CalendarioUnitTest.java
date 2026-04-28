package com.UNIT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import application.controller.MyController;
import application.entities.Tarea;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

import static org.mockito.Mockito.mock;
import org.springframework.test.util.ReflectionTestUtils;

public class CalendarioUnitTest {

    private MyController controller;
    private TareaRepository tareaRepository;
    private Model model;

    @BeforeEach
    void setUp() {
        controller = new MyController();

        tareaRepository = mock(TareaRepository.class);
        model = mock(Model.class);

        ReflectionTestUtils.setField(controller, "tareaRepository", tareaRepository);
        ReflectionTestUtils.setField(controller, "viviendaRepository", mock(ViviendaRepository.class));
        ReflectionTestUtils.setField(controller, "roommateRepository", mock(RoommateRepository.class));
    }

    @Test
    void verCalendario_conTareas() {
        List<Tarea> tareas = new ArrayList<>();
        tareas.add(new Tarea());
        tareas.add(new Tarea());

        when(tareaRepository.findByViviendaId(1L)).thenReturn(tareas);

        String view = controller.verCalendario(1L, model);

        assertEquals("calendario", view);
    }

    @Test
    void verCalendario_sinTareas() {
        when(tareaRepository.findByViviendaId(1L)).thenReturn(new ArrayList<>());

        String view = controller.verCalendario(1L, model);

        assertEquals("calendario", view);
    }
}