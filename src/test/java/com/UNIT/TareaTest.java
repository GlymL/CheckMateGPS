package com.UNIT;

import java.time.LocalDate;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.controller.MyController;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

class TareaTest {

    @Mock
    private ViviendaRepository viviendaRepository;

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

    @Test
    void guardarTarea_createCorrectly() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("name", "Limpiar cocina")
                .param("descripcion", "Limpiar los fogones y la encimera")
                .param("viviendaId", "1"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl())
                .isEqualTo("/vivienda/1/listTareas");

        verify(tareaRepository).save(any(Tarea.class));
    }

    @Test
    void guardarTarea_emptyNameError() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("name", "")
                .param("descripcion", "Descripción válida")
                .param("viviendaId", "1"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");

        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    @Test
    void guardarTarea_invalidNameFormatError() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("name", "Tarea@#$%")
                .param("descripcion", "Descripción válida")
                .param("viviendaId", "1"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");

        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    @Test
    void guardarTarea_invalidDescriptionFormatError() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("name", "Limpiar cocina")
                .param("descripcion", "Descripción @#$%")
                .param("viviendaId", "1"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");

        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    @Test
    void guardarTarea_viviendaNotFoundError() throws Exception {
        when(viviendaRepository.findById(99L))
                .thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("name", "Limpiar cocina")
                .param("descripcion", "Limpiar los fogones")
                .param("viviendaId", "99"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");

        verify(tareaRepository, never()).save(any());
    }

    @Test
    void completarTarea_success() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);

        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setCompletada(false);
        tarea.setVivienda(vivienda);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        MvcResult result = mockMvc.perform(post("/tareas/1/completar")
                .param("roommateId", "1"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl())
                .isEqualTo("/vivienda/1");

        verify(tareaRepository).save(any(Tarea.class));
    }

    @Test
    void unitTest_assignDate_ok() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setId(1L);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        String fechaFutura = LocalDate.now().plusDays(1).toString();

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fechaFutura)
                .param("taskId", "1")
                .param("viviendaId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMsg", "Fecha asignada correctamente."))
                .andExpect(redirectedUrl("/vivienda/10/listTareas"));

        verify(tareaRepository).save(tarea);
    }

    @Test
    void unitTest_assignDate_fechaPasada_error() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setId(1L);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        String fechaAyer = LocalDate.now().minusDays(1).toString();

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fechaAyer)
                .param("taskId", "1")
                .param("viviendaId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMsg"))
                .andExpect(redirectedUrl("/vivienda/10/listTareas"));

        verify(tareaRepository, never()).save(any());
    }

    @Test
    void unitTest_assignDate_tareaNoExiste_error() throws Exception {
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

        String fechaFutura = LocalDate.now().plusDays(5).toString();

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fechaFutura)
                .param("taskId", "99")
                .param("viviendaId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMsg", "Error: La tarea seleccionada no existe."))
                .andExpect(redirectedUrl("/vivienda/10/listTareas"));

        verify(tareaRepository, never()).save(any());
    }
}