package com.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import application.Application;
import application.entities.Roommate;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.ViviendaRepository;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class RoommateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private RoommateRepository roommateRepository;

    @Test
    void addRoommate_Valid() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "CasaTest")
                .param("nombreUsuario", "ana123")
                .param("name", "Ana"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listar"));

        assertThat(roommateRepository.existsByNombreRealAndVivienda("Ana", vivienda)).isTrue();
    }

    @Test
    void addRoommate_Duplicate() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);

        Roommate existente = new Roommate("Maria", vivienda);
        roommateRepository.save(existente);

        mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "CasaTest")
                .param("nombreUsuario", "user2")
                .param("name", "Maria"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-roommate"));

        assertThat(roommateRepository.count()).isEqualTo(1);
    }

    @Test
    void addRoommate_InexistentVivienda() throws Exception {
        mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "CasaInexistente")
                .param("nombreUsuario", "user333")
                .param("name", "Ana"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-roommate"));

        assertThat(roommateRepository.count()).isZero();
    }
}
