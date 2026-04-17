package com.UNIT;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.controller.MyController;
import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

class AssignTareaTest {

    @Mock
    private ViviendaRepository viviendaRepository;

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private RoommateRepository roommateRepository;

    @InjectMocks
    private MyController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void showAssignScreen_success() throws Exception {
        
        Tarea tarea = new Tarea();
        Roommate roommate = new Roommate();
        
        when(tareaRepository.findAll()).thenReturn(Arrays.asList(tarea));
        when(roommateRepository.findAll()).thenReturn(Arrays.asList(roommate));

        MvcResult result = mockMvc.perform(get("/asignarTarea"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("asignarTarea");
        assertThat(result.getModelAndView().getModel().containsKey("tareas")).isTrue();
        assertThat(result.getModelAndView().getModel().containsKey("roommates")).isTrue();
    }

    // CM5-1: asignar tarea a un roommate correctamente
    @Test
    void processAsignment_succes() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(10L);

        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setVivienda(vivienda);

        Roommate roommate = new Roommate("Carlos", vivienda);
        roommate.setId(2L);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        when(roommateRepository.findById(2L)).thenReturn(Optional.of(roommate));

        MvcResult result = mockMvc.perform(post("/procesarAsignacion")
                .param("tareaId", "1")
                .param("roommateId", "2"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/vivienda/10/listTareas");
        
        
        assertThat(result.getFlashMap().get("mensaje")).isEqualTo("Tarea asignada correctamente.");
        
        
        verify(tareaRepository).save(tarea);
    }

    // CM5-2: Test error - faltan datos obligatorios
    @Test
    void processAssignment_missingDataError() throws Exception {
        
        MvcResult result = mockMvc.perform(post("/procesarAsignacion")
                
                .param("roommateId", "2"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("redirect:/asignarTarea");
        
        
        assertThat(result.getFlashMap().get("error")).isEqualTo("Los datos son obligatorios.");
        
        
        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    // CM5-3: Test error - tarea o roommate no existen
    @Test
    void processAssignment_notFoundError() throws Exception {
        
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());
        when(roommateRepository.findById(99L)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(post("/procesarAsignacion")
                .param("tareaId", "99")
                .param("roommateId", "99"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("redirect:/asignarTarea");
        assertThat(result.getFlashMap().get("error")).isEqualTo("La tarea o el roommate seleccionado no existen.");
        
        verify(tareaRepository, never()).save(any(Tarea.class));
    }
}