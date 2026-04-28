package com.INTEGRATION;

import java.util.Optional;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
public class TareaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    
    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private RoommateRepository roommateRepository;

    @Autowired
    private TareaRepository tareaRepository;

    private Vivienda viviendaGuardada;

    @BeforeEach
    void setUp() {
       
        Vivienda nuevaVivienda = new Vivienda();
        nuevaVivienda.setName("Casa Test Supabase");
     
        viviendaGuardada = viviendaRepository.save(nuevaVivienda);
    }

    @Test
    @DisplayName("CM3-1 y CM3-2: Crear tarea exitosamente con campos válidos en BBDD real")
    void createTarea_Valid() throws Exception {
      
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("name", "Limpiar salon")
                .param("descripcion", "Aspirar y limpiar el polvo.")
                .param("viviendaId", idReal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vivienda/" + viviendaGuardada.getId() + "/listTareas"));  
                   
    }

    @Test
    @DisplayName("CM3-3: Error al no rellenar el campo obligatorio (nombre)")
    void createTarea_MissingMandatoryFields() throws Exception {
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("name", "   ") 
                .param("descripcion", "Una descripcion valida")
                .param("viviendaId", idReal))
                .andExpect(status().isOk()) 
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "No se han rellenado todos los campos obligatorios."));
    }

    @Test
    @DisplayName("CM3-4: Error por formato inválido en el nombre de la tarea")
    void createTarea_InvalidNameFormat() throws Exception {
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("name", "Limpiar@Salon!!!") 
                .param("descripcion", "Una descripcion valida")
                .param("viviendaId", idReal))
                .andExpect(status().isOk())
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "El formato del nombre no es válido."));
    }

    @Test
    @DisplayName("CM3-4: Error por formato inválido en la descripción de la tarea")
    void createTarea_InvalidDescriptionFormat() throws Exception {
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("name", "Limpiar salon")
                .param("descripcion", "Limpiar salon <script>alert('hack')</script>") 
                .param("viviendaId", idReal))
                .andExpect(status().isOk())
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "El formato de la descripción no es válido."));
    }

    @Test
    @DisplayName("Prueba extra: Error si la vivienda indicada no existe")
    void createTarea_ViviendaNotFound() throws Exception {
        mockMvc.perform(post("/guardarTarea")
        .param("name", "Bajar la basura")
        .param("viviendaId", "999999999"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/listar"));
    }
    @Test
    @DisplayName("CM1-7: Marcar tarea como completada correctamente")
    void completeTarea_success() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("Casa Test");
        vivienda = viviendaRepository.save(vivienda);
        
        Roommate r = new Roommate("Juan", vivienda);
        r = roommateRepository.save(r);


        Tarea tarea = new Tarea();
        tarea.setName("Limpiar cocina");
        tarea.setDescripcion("Limpiar fogones");
        tarea.setVivienda(vivienda);
        tarea.setCompletada(false);
        tarea = tareaRepository.save(tarea);
        tarea.setAsignadoA(r);
        mockMvc.perform(post("/tareas/" + tarea.getId() + "/completar"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/vivienda/" + vivienda.getId()));

        Optional<Tarea> tareaActualizada = tareaRepository.findById(tarea.getId());
        assert(tareaActualizada).isPresent();
        assert(tareaActualizada.get().getCompletada());
    } 
    //CM12
    @Test
    @DisplayName("Ver tareas en un calendario test")
    void verCalendario_ConTareas() throws Exception {
        mockMvc.perform(get("/vivienda/" + viviendaGuardada.getId() + "/calendario"))
                .andExpect(status().isOk())
                .andExpect(view().name("calendario"))
                .andExpect(model().attributeExists("calendario"));
    }

    @Test
    @DisplayName("Debe mostrar calendario vacío si no existen tareas")
    void verCalendario_SinTareas() throws Exception {
        mockMvc.perform(get("/vivienda/" + viviendaGuardada.getId() + "/calendario"))
                .andExpect(status().isOk())
                .andExpect(view().name("calendario"))
                .andExpect(model().attribute("viviendaId", viviendaGuardada.getId()));
    }

    @Test
    @DisplayName("CM11-1: Consultar descripción de una tarea cuando sí tiene descripción")
    void viewTareaDescription_Exists() throws Exception {
        
        Tarea tareaConDesc = new Tarea();
        tareaConDesc.setName("Limpiar Cristales");
        tareaConDesc.setDescripcion("Usar el spray azul que hay en el armario");
        tareaConDesc.setVivienda(viviendaGuardada);
        tareaConDesc = tareaRepository.save(tareaConDesc);

        
        mockMvc.perform(get("/tarea/" + tareaConDesc.getId() + "/descripcion"))
                .andExpect(status().isOk())
                .andExpect(view().name("descripcion")) 
                .andExpect(model().attributeExists("tarea"))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    
                    assert(content.contains("Usar el spray azul que hay en el armario"));
                });
    }

    @Test
    @DisplayName("CM11-2: Consultar descripción de una tarea cuando no tiene descripción")
    void viewTareaDescription_NotExists() throws Exception {
        
        Tarea tareaSinDesc = new Tarea();
        tareaSinDesc.setName("Sacar Basura");
        tareaSinDesc.setDescripcion(""); 
        tareaSinDesc.setVivienda(viviendaGuardada);
        tareaSinDesc = tareaRepository.save(tareaSinDesc);

        
        mockMvc.perform(get("/tarea/" + tareaSinDesc.getId() + "/descripcion"))
                .andExpect(status().isOk())
                .andExpect(view().name("descripcion"))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    
                    assert(content.contains("La tarea seleccionada no tiene descripción."));
                });
    }
}
