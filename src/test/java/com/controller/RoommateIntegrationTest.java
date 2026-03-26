package com.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DemoApplication;
import com.example.demo.RoommateRepository;
import com.example.demo.Vivienda;
import com.example.demo.ViviendaRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
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
                .param("nombre", "Ana"))
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

        com.example.demo.Roommate existente = new com.example.demo.Roommate("user", "Maria", vivienda);
        roommateRepository.save(existente);

        mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "CasaTest")
                .param("nombreUsuario", "user2")
                .param("nombre", "Maria"))
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
                .param("nombre", "Ana"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-roommate"));

        assertThat(roommateRepository.count()).isZero();
    }
}
