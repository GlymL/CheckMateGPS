package com.INTEGRATION;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import application.Application;
import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
@ContextConfiguration(classes = Application.class)
public class AsignarTareaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private RoommateRepository roommateRepository;

    @Autowired
    private TareaRepository tareaRepository;

    private Tarea tareaGuardada;
    private Roommate roommateGuardado;

    @BeforeEach
    void setUp() {
     
        Vivienda nuevaVivienda = new Vivienda();
        nuevaVivienda.setName("Casa Test Asignacion");
        Vivienda viviendaGuardada = viviendaRepository.save(nuevaVivienda);

       
        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setName("Limpiar ventanas");
        nuevaTarea.setVivienda(viviendaGuardada);
        tareaGuardada = tareaRepository.save(nuevaTarea);

     
        Roommate nuevoRoommate = new Roommate("Juan Perez", viviendaGuardada);
        roommateGuardado = roommateRepository.save(nuevoRoommate);
    }

    @Test
    @DisplayName("CM5-1: Asignar tarea exitosamente con un roommate y tarea existentes en BBDD real")
    void asignTarea_succes() throws Exception {
        
        String tareaId = String.valueOf(tareaGuardada.getId());
        String roommateId = String.valueOf(roommateGuardado.getId());

        mockMvc.perform(post("/asignarTarea")
                .param("tareaId", tareaId)
                .param("roommateId", roommateId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listar")); 
        
      
    }

    @Test
    @DisplayName("CM5-2: Error al no seleccionar ninguna tarea ni roommate")
    void asignTarea_withoutData() throws Exception {
        
       
        mockMvc.perform(post("/asignarTarea"))
                .andExpect(status().isOk())
                .andExpect(view().name("asignarTarea"))
                .andExpect(model().attributeExists("errorAsignacion"))
                .andExpect(model().attribute("errorAsignacion", "Los datos son obligatorios."));
    }

    @Test
    @DisplayName("CM5-2: Error al seleccionar tarea pero falta el roommate")
    void asignTarea_missingRoomate() throws Exception {
        
        String tareaId = String.valueOf(tareaGuardada.getId());

        mockMvc.perform(post("/asignarTarea")
                .param("tareaId", tareaId)) 
                .andExpect(status().isOk())
                .andExpect(view().name("asignarTarea"))
                .andExpect(model().attributeExists("errorAsignacion"))
                .andExpect(model().attribute("errorAsignacion", "Los datos son obligatorios."));
    }

    @Test
    @DisplayName("CM5-2: Error al seleccionar roommate pero falta la tarea")
    void  asignTarea_missingTarea() throws Exception {
        
        String roommateId = String.valueOf(roommateGuardado.getId());

        mockMvc.perform(post("/asignarTarea")
                .param("roommateId", roommateId)) 
                .andExpect(status().isOk())
                .andExpect(view().name("asignarTarea"))
                .andExpect(model().attributeExists("errorAsignacion"))
                .andExpect(model().attribute("errorAsignacion", "Los datos son obligatorios."));
    }
    
    // CM7 
    @Test
    void assignDate_success() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        vivienda = viviendaRepository.save(vivienda);

        Tarea tarea = new Tarea();
        tarea.setName("Limpiar");
        tarea.setCompletada(false);
        tarea.setVivienda(vivienda);
        tarea = tareaRepository.save(tarea);
        String fecha = LocalDate.now().plusDays(1).toString();

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fecha)
                .param("taskId", tarea.getId().toString())
                .param("viviendaId", vivienda.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId() + "/listTareas"));

        Optional<Tarea> tareaUpdated = tareaRepository.findById(tarea.getId());
        assertThat(tareaUpdated.get().getFechaRealizacion()).isNotNull();
    }
    @Test
    void assignDate_fechaPasada_error() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        vivienda = viviendaRepository.save(vivienda);

        Tarea tarea = new Tarea();
        tarea.setName("Limpiar");
        tarea.setVivienda(vivienda);
        tarea = tareaRepository.save(tarea);

        String fecha = LocalDate.now().minusDays(1).toString();

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fecha)
                .param("taskId", tarea.getId().toString())
                .param("viviendaId", vivienda.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMsg"));

        Optional<Tarea> tareaUpdated = tareaRepository.findById(tarea.getId());
        assertThat(tareaUpdated.get().getFechaRealizacion()).isNull();
    }
    @Test
    void assignDate_tareaNoExiste_error() throws Exception {
        
        String fecha = LocalDate.now().plusDays(2).toString();
        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fecha)
                .param("taskId", "999")
                .param("viviendaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMsg", "Error: La tarea seleccionada no existe."));
    }

}