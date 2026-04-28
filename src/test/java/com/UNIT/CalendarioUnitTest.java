package com.UNIT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import application.controller.MyController;
import application.entities.Tarea;
import application.repositories.TareaRepository;

public class CalendarioUnitTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // CM12-1
    @Test
    void verCalendario_conTareas() {
        Long viviendaId = 1L;

        Tarea t1 = new Tarea();
        t1.setName("Limpiar cocina");

        Tarea t2 = new Tarea();
        t2.setName("Bajar basura");

        when(tareaRepository.findByViviendaId(viviendaId))
                .thenReturn(Arrays.asList(t1, t2));

        String view = controller.verCalendario(viviendaId, model);

        assertEquals("calendario", view);
        verify(model).addAttribute(eq("calendario"), any());
        verify(model).addAttribute("viviendaId", viviendaId);
    }

    // CM12-2
    @Test
    void verCalendario_sinTareas() {
        Long viviendaId = 1L;

        when(tareaRepository.findByViviendaId(viviendaId))
                .thenReturn(Collections.emptyList());

        String view = controller.verCalendario(viviendaId, model);

        assertEquals("calendario", view);
        verify(model).addAttribute(eq("calendario"), any());
        verify(model).addAttribute("viviendaId", viviendaId);
    }
}