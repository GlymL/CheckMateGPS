package com.UNIT;

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

    // CT1-1: Test exitoso - guardar tarea con datos válidos
    @Test
    void guardarTarea_createCorrectly() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar cocina")
                .param("descripcion", "Limpiar los fogones y la encimera")
                .param("vivienda.id", "1"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");
        verify(tareaRepository).save(any(Tarea.class));
    }

    // CT1-2: Test error - nombre vacío
    @Test
    void guardarTarea_emptyNameError() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "")
                .param("descripcion", "Descripción válida")
                .param("vivienda.id", "1"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");
        assertThat(result.getModelAndView().getModel().get("error"))
                .isEqualTo("No se han rellenado todos los campos obligatorios.");
        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    // CT1-3: Test error - formato de nombre inválido
    @Test
    void guardarTarea_invalidNameFormatError() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Tarea@#$%")
                .param("descripcion", "Descripción válida")
                .param("vivienda.id", "1"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");
        assertThat(result.getModelAndView().getModel().get("error"))
                .isEqualTo("El formato del nombre no es válido.");
        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    // CT1-4: Test error - formato de descripción inválido
    @Test
    void guardarTarea_invalidDescriptionFormatError() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar cocina")
                .param("descripcion", "Descripción con caracteres ilegales @#$%")
                .param("vivienda.id", "1"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");
        assertThat(result.getModelAndView().getModel().get("error"))
                .isEqualTo("El formato de la descripción no es válido.");
        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    // CT1-5: Test error - vivienda no existe
    @Test
    void guardarTarea_viviendaNotFoundError() throws Exception {
        
        when(viviendaRepository.findById(99L))
                .thenReturn(Optional.empty());

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar cocina")
                .param("descripcion", "Limpiar los fogones")
                .param("vivienda.id", "99"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("crearTarea");
        assertThat(result.getModelAndView().getModel().get("error"))
                .isEqualTo("Vivienda no encontrada.");
        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    // CT1-6: Test - descripción opcional (vacía)
    @Test
    void guardarTarea_withoutDescriptionSuccess() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar cocina")
                .param("vivienda.id", "1"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");
        verify(tareaRepository).save(any(Tarea.class));
    }

    
    @Test
    void guardarTarea_descriptionWithValidPunctuationSuccess() throws Exception {
        
        Vivienda vivienda = new Vivienda();
        vivienda.setId(1L);
        vivienda.setName("Casa1");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        
        MvcResult result = mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Hacer compras")
                .param("descripcion", "Comprar leche, pan y huevos. No olvidar el queso!")
                .param("vivienda.id", "1"))
                .andReturn();

        
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");
        verify(tareaRepository).save(any(Tarea.class));
    }


    @Test
    void completarTarea_success() throws Exception {
    // Arrange
    Vivienda vivienda = new Vivienda();
    vivienda.setId(1L);
    vivienda.setName("Casa1");
    
    Tarea tarea = new Tarea();
    tarea.setId(1L);
    tarea.setName("Limpiar cocina");
    tarea.setCompletada(false);
    tarea.setVivienda(vivienda);
    
    when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
    when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);
    
    // Act
    MvcResult result = mockMvc.perform(post("/tarea/1/completar"))
            .andReturn();
    
    // Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(302);
    assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");
    verify(tareaRepository).findById(1L);
    verify(tareaRepository).save(any(Tarea.class));
        }
}
