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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.DisplayName;
import application.Application;
import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

import java.time.LocalDate;

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

      // CM4-1: Tarea marcada como realizada con éxito
    @Test
    void flujoCompleto_completarTarea_ok() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);


        Roommate roommate = new Roommate("user1", vivienda);
        roommateRepository.save(roommate);


        Tarea tarea = new Tarea();
        tarea.setName("Limpiar baño");
        tarea.setVivienda(vivienda);
        tarea.setAsignadoA(roommate);
        tarea.setCompletada(false);
        tareaRepository.save(tarea);


        mockMvc.perform(post("/tareas/" + tarea.getId() + "/completar")
                .param("roommateId", String.valueOf(roommate.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId()));


        Tarea tareaGuardada = tareaRepository.findById(tarea.getId()).get();
        assertThat(tareaGuardada.getName()).isEqualTo("Limpiar baño");
        assertThat(tareaGuardada.getDescripcion()).isNull();
        assertThat(tareaGuardada.getVivienda().getId()).isEqualTo(vivienda.getId());
        assertThat(tareaGuardada.getCompletada()).isTrue();
    }


    // CM4-2: Error al marcar tarea que ya estaba realizada
    @Test
    void flujoCompleto_completarTarea_yaRealizada() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest2");
        viviendaRepository.save(vivienda);


        Roommate roommate = new Roommate("user2", vivienda);
        roommateRepository.save(roommate);


        Tarea tarea = new Tarea();
        tarea.setName("Fregar platos");
        tarea.setVivienda(vivienda);
        tarea.setAsignadoA(roommate);
        tarea.setCompletada(true); // Ya está completada de antes
        tareaRepository.save(tarea);

        mockMvc.perform(post("/tareas/" + tarea.getId() + "/completar")
                .param("roommateId", String.valueOf(roommate.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "La tarea ya está realizada"))
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId()));

        Tarea tareaGuardada = tareaRepository.findById(tarea.getId()).get();
        assertThat(tareaGuardada.getAsignadoA().getId())
            .isEqualTo(roommate.getId());
    }


    // CM4-3: Aviso al marcar tarea que no tiene roommate asignado
    @Test
    void flujoCompleto_completarTarea_sinAsignar() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest3");
        viviendaRepository.save(vivienda);


        Roommate roommate = new Roommate("user3", vivienda);
        roommateRepository.save(roommate);


        Tarea tarea = new Tarea();
        tarea.setName("Bajar basura");
        tarea.setVivienda(vivienda);
        tarea.setAsignadoA(null); 
        tarea.setCompletada(false);
        tareaRepository.save(tarea);

        mockMvc.perform(post("/tareas/" + tarea.getId() + "/completar")
                .param("roommateId", String.valueOf(roommate.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "La tarea no tiene roommate asignado"))
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId()));

        Tarea tareaGuardada = tareaRepository.findById(tarea.getId()).get();

        assertThat(tareaGuardada.getCompletada()).isFalse();
        assertThat(tareaGuardada.getAsignadoA()).isNull();
    }

    @Test
    @DisplayName("SISTEMA-TAREA-CM7: Flujo completo - Asignar fecha futura correcta")
    void fullFlow_assignDate_ok() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest CM7");
        viviendaRepository.save(vivienda);

        Tarea tarea = new Tarea();
        tarea.setName("Tarea CM7"); 
        tarea.setVivienda(vivienda);
        tareaRepository.save(tarea);

        String fechaFutura = LocalDate.now().plusDays(5).toString();

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fechaFutura)
                .param("taskId", String.valueOf(tarea.getId()))
                .param("viviendaId", String.valueOf(vivienda.getId())))
                .andDo(print())
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId() + "/listTareas"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("successMsg"));

        Tarea tareaActualizada = tareaRepository.findById(tarea.getId()).get();
        assertThat(tareaActualizada.getFechaRealizacion().toString()).isEqualTo(fechaFutura);
    }

    @Test
    @DisplayName("SISTEMA-TAREA-CM7: Flujo de error - Asignar fecha del pasado")
    void fullFlow_assignDate_pastDate_shouldShowError() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest CM7 Error");
        viviendaRepository.save(vivienda);

        Tarea tarea = new Tarea();
        tarea.setName("Tarea CM7 Pasado"); 
        tarea.setVivienda(vivienda);
        tareaRepository.save(tarea);

        String fechaPasada = LocalDate.now().minusDays(1).toString(); 

        mockMvc.perform(post("/assignDate/submit")
                .param("fecha", fechaPasada)
                .param("taskId", String.valueOf(tarea.getId()))
                .param("viviendaId", String.valueOf(vivienda.getId())))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId() + "/listTareas"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("errorMsg"));

        assertThat(tareaRepository.count()).isEqualTo(1);
        Tarea tareaGuardada = tareaRepository.findAll().get(0);
        assertThat(tareaGuardada.getName()).isEqualTo("Tarea sin descripción");
        assertThat(tareaGuardada.getDescripcion()).isNullOrEmpty();

        Tarea tareaNoActualizada = tareaRepository.findById(tarea.getId()).get();
        assertThat(tareaNoActualizada.getFechaRealizacion()).isNull();
    }
}