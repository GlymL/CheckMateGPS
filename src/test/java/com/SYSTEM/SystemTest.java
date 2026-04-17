package com.SYSTEM;

import static org.assertj.core.api.Assertions.assertThat;
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

import application.Application;
import application.entities.Roommate;
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


        assertThat(tareaRepository.count()).isEqualTo(1);
        Tarea tareaGuardada = tareaRepository.findAll().get(0);
        assertThat(tareaGuardada.getName()).isEqualTo("Limpiar cocina");
        assertThat(tareaGuardada.getDescripcion()).isEqualTo("Limpiar nevera y fregar los platos");
        assertThat(tareaGuardada.getVivienda().getId()).isEqualTo(vivienda.getId());
        assertThat(tareaGuardada.getCompletada()).isFalse();
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
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId()));


        boolean sigueCompletada = tareaRepository.findById(tarea.getId()).get().getCompletada();
       
        assertThat(sigueCompletada).isTrue();
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
        tarea.setAsignadoA(null); // Tarea sin nadie asignado
        tarea.setCompletada(false);
        tareaRepository.save(tarea);


        mockMvc.perform(post("/tareas/" + tarea.getId() + "/completar")
                .param("roommateId", String.valueOf(roommate.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/vivienda/" + vivienda.getId()));


        boolean estaCompletada = tareaRepository.findById(tarea.getId()).get().getCompletada();
       
        assertThat(estaCompletada).isFalse();
    }
}