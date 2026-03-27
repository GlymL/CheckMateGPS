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

import entities.Vivienda;
import launch.DemoApplication;
import repositories.RoommateRepository;
import repositories.ViviendaRepository;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@Transactional
class SystemTest {

    @Autowired
    private MockMvc mockMvc;

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
}