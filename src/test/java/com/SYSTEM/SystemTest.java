package com.SYSTEM;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import application.Application;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
class SystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private RoommateRepository roommateRepository;

    
    @Test
    void fullFlow_addRoommate_ok() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);
     mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "CasaTest")
                .param("nombreUsuario", "ana123")
                .param("nombre", "Ana"))
                .andDo(print()) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listar"));
        boolean exista = roommateRepository
                .existsByNombreRealAndVivienda("Ana", vivienda);

        assertThat(exista).isTrue();
    }

    @Test
   
    void fullFlow_createTaskSuccessfully() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);
    
        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Limpiar cocina")
                .param("descripcion", "Limpiar nevera y fregar los platos")
                .param("vivienda.id", String.valueOf(vivienda.getId())))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listar"));


        assertThat(tareaRepository.count()).isEqualTo(1);
        Tarea tareaGuardada = tareaRepository.findAll().get(0);
        assertThat(tareaGuardada.getName()).isEqualTo("Limpiar cocina");
        assertThat(tareaGuardada.getDescripcion()).isEqualTo("Limpiar nevera y fregar los platos");
        assertThat(tareaGuardada.getVivienda().getId()).isEqualTo(vivienda.getId());
        assertThat(tareaGuardada.getCompletada()).isFalse();
    }

    @Test
    void fullFlow_multipleTasksForSameHouse() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);
        String[] tareas = {
            "Limpiar salón",
            "Sacar la basura",
            "Regar las plantas"
        };
        
        for (String nombreTarea : tareas) {
            mockMvc.perform(post("/guardarTarea")
                    .param("nombre", nombreTarea)
                    .param("vivienda.id", String.valueOf(vivienda.getId())))
                    .andExpect(status().is3xxRedirection());
        }
        
        // Verificar que se guardaron correctamente
        assertThat(tareaRepository.count()).isEqualTo(3);
        
        // Verificar que todas pertenecen a la vivienda correcta
        for (Tarea tarea : tareaRepository.findAll()) {
            assertThat(tarea.getVivienda().getId()).isEqualTo(vivienda.getId());
        }
    }
    
    @Test
    void fullFlow_createTaskWithoutName_shouldShowError() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        // 2. Enviar formulario con nombre vacío
        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "")
                .param("descripcion", "Descripción válida")
                .param("vivienda.id", String.valueOf(vivienda.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));

    
        assertThat(tareaRepository.count()).isEqualTo(0);
    }

    @Test
    void fullFlow_createTaskForNonExistentHouse_shouldShowError() throws Exception {
        Long idInexistente = 999999L;
        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Tarea prueba")
                .param("vivienda.id", String.valueOf(idInexistente)))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(tareaRepository.count()).isEqualTo(0);
    }
        @Test
    @DisplayName("SISTEMA-TAREA-07: Flujo completo - Crear tarea con descripción opcional vacía")
    void fullFlow_createTaskWithEmptyDescription() throws Exception {
              Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

      
        mockMvc.perform(post("/guardarTarea")
                .param("nombre", "Tarea sin descripción")
                .param("descripcion", "")
                .param("vivienda.id", String.valueOf(vivienda.getId())))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listar"));

        assertThat(tareaRepository.count()).isEqualTo(1);
        Tarea tareaGuardada = tareaRepository.findAll().get(0);
        assertThat(tareaGuardada.getName()).isEqualTo("Tarea sin descripción");
        assertThat(tareaGuardada.getDescripcion()).isNullOrEmpty();
    }
}