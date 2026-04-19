package com.UNIT;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.controller.MyController;
import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

class ListTareasUnitTest {

    @Mock
    private ViviendaRepository viviendaRepository;

    @Mock
    private RoommateRepository roommateRepository;

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private MyController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    //Ver listado de tareas correctamente
   
    @Test
    void listTareas_showTareasCorrectly() throws Exception {
        // Arrange
        Long viviendaId = 1L;
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(viviendaId);
        vivienda.setName("Casa Test");
        
        Roommate roommate1 = new Roommate("Juan", vivienda);
        Roommate roommate2 = new Roommate("Maria", vivienda);
        
        Tarea tarea1 = new Tarea();
        tarea1.setId(1L);
        tarea1.setName("Limpiar cocina");
        tarea1.setDescripcion("Limpiar fogones y encimera");
        tarea1.setVivienda(vivienda);
        tarea1.setAsignadoA(roommate1);
        tarea1.setCompletada(false);
        
        Tarea tarea2 = new Tarea();
        tarea2.setId(2L);
        tarea2.setName("Bajar basura");
        tarea2.setDescripcion("Bajar la basura después de cenar");
        tarea2.setVivienda(vivienda);
        tarea2.setAsignadoA(roommate2);
        tarea2.setCompletada(true);
        
        vivienda.setTareas(Arrays.asList(tarea1, tarea2));
        
        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(vivienda));
        when(roommateRepository.findByViviendaId(viviendaId)).thenReturn(Arrays.asList(roommate1, roommate2));
        
        // Act
        MvcResult result = mockMvc.perform(get("/vivienda/{id}/listTareas", viviendaId))
                .andReturn();
        
        // Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("listTareas");
        assertThat(result.getModelAndView().getModel().get("vivienda")).isEqualTo(vivienda);
        assertThat(result.getModelAndView().getModel().get("viviendaId")).isEqualTo(viviendaId);
        assertThat(result.getModelAndView().getModel().get("roommates")).isEqualTo(Arrays.asList(roommate1, roommate2));
    }

   
    //Mostrar mensaje cuando no hay tareas
    @Test
    void listTareas_showEmptyMessageWhenNoTareas() throws Exception {
        // Arrange
        Long viviendaId = 1L;
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(viviendaId);
        vivienda.setName("Casa Sin Tareas");
        vivienda.setTareas(Collections.emptyList());
        
        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(vivienda));
        when(roommateRepository.findByViviendaId(viviendaId)).thenReturn(Collections.emptyList());
        
        // Act
        MvcResult result = mockMvc.perform(get("/vivienda/{id}/listTareas", viviendaId))
                .andReturn();
        
        // Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("listTareas");
        assertThat(((java.util.List<?>) result.getModelAndView().getModel().get("roommates")).isEmpty()).isTrue();
    }

    
    // Redirección si la vivienda no existe
   
    @Test
    void listTareas_redirectWhenViviendaNotFound() throws Exception {
        // Arrange
        Long viviendaIdInexistente = 999L;
        
        when(viviendaRepository.findById(viviendaIdInexistente)).thenReturn(Optional.empty());
        
        // Act
        MvcResult result = mockMvc.perform(get("/vivienda/{id}/listTareas", viviendaIdInexistente))
                .andReturn();
        
        // Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");
    }


    //Ver tareas con diferentes estados (pendiente/completada)
    @Test
    void listTareas_showTareaStatusCorrectly() throws Exception {
        // Arrange
        Long viviendaId = 1L;
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(viviendaId);
        vivienda.setName("Casa Test Estados");
        
        Roommate roommate = new Roommate("Pedro", vivienda);
        
        Tarea tareaPendiente = new Tarea();
        tareaPendiente.setId(1L);
        tareaPendiente.setName("Tarea Pendiente");
        tareaPendiente.setVivienda(vivienda);
        tareaPendiente.setAsignadoA(roommate);
        tareaPendiente.setCompletada(false);
        
        Tarea tareaCompletada = new Tarea();
        tareaCompletada.setId(2L);
        tareaCompletada.setName("Tarea Completada");
        tareaCompletada.setVivienda(vivienda);
        tareaCompletada.setAsignadoA(roommate);
        tareaCompletada.setCompletada(true);
        
        vivienda.setTareas(Arrays.asList(tareaPendiente, tareaCompletada));
        
        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(vivienda));
        when(roommateRepository.findByViviendaId(viviendaId)).thenReturn(Arrays.asList(roommate));
        
        // Act
        MvcResult result = mockMvc.perform(get("/vivienda/{id}/listTareas", viviendaId))
                .andReturn();
        
        // Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        Vivienda viviendaEnModelo = (Vivienda) result.getModelAndView().getModel().get("vivienda");
        java.util.List<Tarea> tareas = viviendaEnModelo.getTareas();
        
        assertThat(tareas.get(0).getCompletada()).isFalse();
        assertThat(tareas.get(1).getCompletada()).isTrue();
    }


    // Ver tareas asignadas a diferentes roommates
    @Test
    void listTareas_showAssignedRoommateForEachTarea() throws Exception {
        // Arrange
        Long viviendaId = 1L;
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(viviendaId);
        vivienda.setName("Casa Test Asignaciones");
        
        Roommate roommateJuan = new Roommate("Juan", vivienda);
        Roommate roommateAna = new Roommate("Ana", vivienda);
        
        Tarea tareaJuan = new Tarea();
        tareaJuan.setId(1L);
        tareaJuan.setName("Limpiar baño");
        tareaJuan.setVivienda(vivienda);
        tareaJuan.setAsignadoA(roommateJuan);
        
        Tarea tareaAna = new Tarea();
        tareaAna.setId(2L);
        tareaAna.setName("Hacer compra");
        tareaAna.setVivienda(vivienda);
        tareaAna.setAsignadoA(roommateAna);
        
        vivienda.setTareas(Arrays.asList(tareaJuan, tareaAna));
        
        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(vivienda));
        when(roommateRepository.findByViviendaId(viviendaId)).thenReturn(Arrays.asList(roommateJuan, roommateAna));
        
        // Act
        MvcResult result = mockMvc.perform(get("/vivienda/{id}/listTareas", viviendaId))
                .andReturn();
        
        // Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        Vivienda viviendaEnModelo = (Vivienda) result.getModelAndView().getModel().get("vivienda");
        java.util.List<Tarea> tareas = viviendaEnModelo.getTareas();
        
        assertThat(tareas.get(0).getAsignadoA().getNombreReal()).isEqualTo("Juan");
        assertThat(tareas.get(1).getAsignadoA().getNombreReal()).isEqualTo("Ana");
    }

    
    // Ver tareas sin asignar correctamente
    @Test
    void listTareas_showUnassignedTareas() throws Exception {
        // Arrange
        Long viviendaId = 1L;
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(viviendaId);
        vivienda.setName("Casa Test Sin Asignar");
        
        Roommate roommate = new Roommate("Carlos", vivienda);
        
        Tarea tareaAsignada = new Tarea();
        tareaAsignada.setId(1L);
        tareaAsignada.setName("Tarea Asignada");
        tareaAsignada.setVivienda(vivienda);
        tareaAsignada.setAsignadoA(roommate);
        
        Tarea tareaSinAsignar = new Tarea();
        tareaSinAsignar.setId(2L);
        tareaSinAsignar.setName("Tarea Sin Asignar");
        tareaSinAsignar.setVivienda(vivienda);
        tareaSinAsignar.setAsignadoA(null);
        
        vivienda.setTareas(Arrays.asList(tareaAsignada, tareaSinAsignar));
        
        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(vivienda));
        when(roommateRepository.findByViviendaId(viviendaId)).thenReturn(Arrays.asList(roommate));
        
        // Act
        MvcResult result = mockMvc.perform(get("/vivienda/{id}/listTareas", viviendaId))
                .andReturn();
        
        // Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        Vivienda viviendaEnModelo = (Vivienda) result.getModelAndView().getModel().get("vivienda");
        java.util.List<Tarea> tareas = viviendaEnModelo.getTareas();
        
        assertThat(tareas.get(0).getAsignadoA()).isNotNull();
        assertThat(tareas.get(1).getAsignadoA()).isNull();
    }
}