package com.INTEGRATION;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

import application.Application;
import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;
import java.util.List;

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
                .andExpect(redirectedUrl("/listar"));   
        
       
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
        
        String idInexistente = "999999999"; 

        mockMvc.perform(post("/guardarTarea")
                .param("name", "Bajar la basura")
                .param("viviendaId", idInexistente))
                .andExpect(status().isOk())
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "Vivienda no encontrada."));
    }

    // para CM6
    @Test
    void relacionCompleta() {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("Casa");
        viviendaRepository.save(vivienda);

        Roommate r = new Roommate("user1", "Ana", vivienda);
        roommateRepository.save(r);

        Tarea t = new Tarea();
        t.setName("Limpiar");
        t.setVivienda(vivienda);
        t.setAsignadoA(r);
        t.setCompletada(false);

        tareaRepository.save(t);

        Vivienda encontrada = viviendaRepository.findById(vivienda.getId()).get();

        assertThat(encontrada.getRoommates()).isNotEmpty();
    }
    @Test
    void guardarTareaCorrectamente() {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("Casa");
        viviendaRepository.save(vivienda);

        Roommate r = new Roommate("user1", "Ana", vivienda);
        roommateRepository.save(r);

        Tarea t = new Tarea();
        t.setName("Limpiar");
        t.setVivienda(vivienda);
        t.setAsignadoA(r);
        t.setCompletada(false);

        tareaRepository.save(t);

        Tarea saved = tareaRepository.findById(t.getId()).get();

        assertThat(saved.getName()).isEqualTo("Limpiar");
    }
    @Test
    void filtrarSoloNoCompletadas() {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("Casa");
        viviendaRepository.save(vivienda);

        Roommate r = new Roommate("user1", "Ana", vivienda);
        roommateRepository.save(r);

        Tarea t1 = new Tarea();
        t1.setName("T1");
        t1.setVivienda(vivienda);
        t1.setAsignadoA(r);
        t1.setCompletada(false);

        Tarea t2 = new Tarea();
        t2.setName("T2");
        t2.setVivienda(vivienda);
        t2.setAsignadoA(r);
        t2.setCompletada(true);

        tareaRepository.save(t1);
        tareaRepository.save(t2);

        long count = tareaRepository.findAll().stream()
                .filter(t -> !t.getCompletada())
                .count();

        assertThat(count).isEqualTo(1);
    }
}