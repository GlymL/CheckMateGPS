package com.UNIT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.controller.MyController;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

class EstadoTareasTest {

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

    // CM8-1: Ver estado de tareas de la vivienda (con tareas que deben ordenarse)
    @Test
    @SuppressWarnings("unchecked")
    void verEstadoTareas_conTareas_success() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);

        Tarea tareaTerminada = new Tarea();
        tareaTerminada.setId(10L);
        tareaTerminada.setName("Limpiar baño");
        tareaTerminada.setCompletada(true);

        Tarea tareaPendiente = new Tarea();
        tareaPendiente.setId(11L);
        tareaPendiente.setName("Comprar pan");
        tareaPendiente.setCompletada(false);

        // Simulamos que la BD nos devuelve las tareas desordenadas (la terminada primero)
        vivienda.setTareas(Arrays.asList(tareaTerminada, tareaPendiente));

        when(viviendaRepository.findById(1L)).thenReturn(Optional.of(vivienda));

        MvcResult result = mockMvc.perform(get("/vivienda/1/estado-tareas"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("estadoTareas");
        
        // Verificamos que los datos correctos viajan a la vista
        assertThat(result.getModelAndView().getModel().containsKey("tareas")).isTrue();
        assertThat(result.getModelAndView().getModel().get("viviendaId")).isEqualTo(1L);

        // CM8-1: Verificamos el ordenado (las pendientes deben estar arriba)
        List<Tarea> tareasDevueltas = (List<Tarea>) result.getModelAndView().getModel().get("tareas");
        assertThat(tareasDevueltas).hasSize(2);
        assertThat(tareasDevueltas.get(0).getId()).isEqualTo(11L); // La pendiente va a la posición 0
        assertThat(tareasDevueltas.get(1).getId()).isEqualTo(10L); // La completada va a la posición 1
    }

    // CM8-2: Ver estado de tareas cuando la vivienda seleccionada no tiene tareas creadas
    @Test
    @SuppressWarnings("unchecked")
    void verEstadoTareas_sinTareas_success() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(2L);
        vivienda.setTareas(new ArrayList<>()); // Casa vacía de tareas

        when(viviendaRepository.findById(2L)).thenReturn(Optional.of(vivienda));

        MvcResult result = mockMvc.perform(get("/vivienda/2/estado-tareas"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("estadoTareas");
        
        // Verificamos que la lista se inyecta en el modelo estando vacía
        // Esto desencadenará el th:if="${#lists.isEmpty(tareas)}" en tu HTML
        List<Tarea> tareasDevueltas = (List<Tarea>) result.getModelAndView().getModel().get("tareas");
        assertThat(tareasDevueltas).isEmpty(); 
    }

}