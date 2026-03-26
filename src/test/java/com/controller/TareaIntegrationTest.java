package com.controller;

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
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DemoApplication;
import com.example.demo.RoommateRepository;
import com.example.demo.TareaRepository;
import com.example.demo.Vivienda;
import com.example.demo.ViviendaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Revierte los cambios en la BBDD al terminar cada test para no dejar basura
@ContextConfiguration(classes = DemoApplication.class)
public class TareaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // AHORA USAMOS LOS REPOSITORIOS REALES (Autowired en lugar de MockitoBean)
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
    void crearTarea_Exito() throws Exception {
      
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar salon")
                .param("descripcion", "Aspirar y limpiar el polvo.")
                .param("viviendaId", idReal))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/listar"));   
        
       
    }

    @Test
    @DisplayName("CM3-3: Error al no rellenar el campo obligatorio (nombre)")
    void crearTarea_FaltanCamposObligatorios() throws Exception {
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "   ") 
                .param("descripcion", "Una descripcion valida")
                .param("viviendaId", idReal))
                .andExpect(status().isOk()) 
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "No se han rellenado todos los campos obligatorios."));
    }

    @Test
    @DisplayName("CM3-4: Error por formato inválido en el nombre de la tarea")
    void crearTarea_FormatoNombreInvalido() throws Exception {
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar@Salon!!!") 
                .param("descripcion", "Una descripcion valida")
                .param("viviendaId", idReal))
                .andExpect(status().isOk())
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "El formato del nombre no es válido."));
    }

    @Test
    @DisplayName("CM3-4: Error por formato inválido en la descripción de la tarea")
    void crearTarea_FormatoDescripcionInvalido() throws Exception {
        String idReal = String.valueOf(viviendaGuardada.getId());

        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar salon")
                .param("descripcion", "Limpiar salon <script>alert('hack')</script>") 
                .param("viviendaId", idReal))
                .andExpect(status().isOk())
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "El formato de la descripción no es válido."));
    }

    @Test
    @DisplayName("Prueba extra: Error si la vivienda indicada no existe")
    void crearTarea_ViviendaNoEncontrada() throws Exception {
        // Un ID que sabemos que no va a existir en tu BBDD
        String idInexistente = "999999999"; 

        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Bajar la basura")
                .param("viviendaId", idInexistente))
                .andExpect(status().isOk())
                .andExpect(view().name("crearTarea"))
                .andExpect(model().attribute("error", "Vivienda no encontrada."));
    }
}