package com.SYSTEM;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.example.demo.DemoApplication;
import com.example.demo.RoommateRepository;
import com.example.demo.Vivienda;
import com.example.demo.ViviendaRepository;
import com.example.demo.Tarea;
import com.example.demo.TareaRepository;
import com.example.demo.Roommate;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@Transactional
class SystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;

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

    // por CM5
    @Test
    void assignTask_ok() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        Roommate roommate = new Roommate("user1", "Ana", vivienda);
        roommateRepository.save(roommate);

        Tarea tarea = new Tarea();
        tarea.setName("Limpiar");
        tarea.setVivienda(vivienda);
        tareaRepository.save(tarea);

        mockMvc.perform(post("/assignTask/submit")
                .param("taskId", tarea.getId().toString())
                .param("roommateId", roommate.getId().toString())
                .param("viviendaId", vivienda.getId().toString()))
            .andDo(print())
            .andExpect(status().is3xxRedirection());

        Tarea actualizada = tareaRepository.findById(tarea.getId()).get();
        assertThat(actualizada.getRoommateId()).isEqualTo(roommate.getId().toString());
    }
    @Test
    void assignTask_sinTask() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        Roommate roommate = new Roommate("user1", "Ana", vivienda);
        roommateRepository.save(roommate);

        mockMvc.perform(post("/assignTask/submit")
                .param("roommateId", roommate.getId().toString())
                .param("viviendaId", vivienda.getId().toString()))
            .andExpect(status().is3xxRedirection());
    }
    @Test
    void assignTask_sinRoommate() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        Tarea tarea = new Tarea();
        tarea.setName("Limpiar");
        tarea.setVivienda(vivienda);
        tareaRepository.save(tarea);

        mockMvc.perform(post("/assignTask/submit")
                .param("taskId", tarea.getId().toString())
                .param("viviendaId", vivienda.getId().toString()))
            .andExpect(status().is3xxRedirection());
    }
    @Test
    void assignTask_sinDatos() throws Exception {

        mockMvc.perform(post("/assignTask/submit")
                .param("viviendaId", "1"))
            .andExpect(status().is3xxRedirection());
    }
    @Test
    void assignTask_taskNoExiste() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        Roommate roommate = new Roommate("user1", "Ana", vivienda);
        roommateRepository.save(roommate);

        mockMvc.perform(post("/assignTask/submit")
                .param("taskId", "999")
                .param("roommateId", roommate.getId().toString())
                .param("viviendaId", vivienda.getId().toString()))
            .andExpect(status().is3xxRedirection());
    }
}