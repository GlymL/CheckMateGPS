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
        
        // Obtenemos el ID real que la BD le ha asignado a la vivienda
        Long idVivienda = vivienda.getId();

        mockMvc.perform(post("/guardarRoommate")
                .param("viviendaId", String.valueOf(idVivienda))
                .param("nombreReal", "Ana"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                // Ahora redirige a los detalles de esa vivienda concreta
                .andExpect(redirectedUrl("/vivienda/" + idVivienda));

        assertThat(roommateRepository.existsByNombreRealAndVivienda("Ana", vivienda)).isTrue();
    }

    @Test
    void addRoommate_Duplicate() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("CasaTest");
        viviendaRepository.save(vivienda);
        
        Long idVivienda = vivienda.getId();

        // El constructor de Roommate ya no tiene 'nombreUsuario'
        Roommate existente = new Roommate("Maria", vivienda);
        roommateRepository.save(existente);

        mockMvc.perform(post("/guardarRoommate")
                .param("viviendaId", String.valueOf(idVivienda))
                .param("nombreReal", "Maria"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                // En caso de error de duplicado, vuelve al formulario de esa vivienda
                .andExpect(redirectedUrl("/vivienda/" + idVivienda + "/nuevo-roommate"));

        assertThat(roommateRepository.count()).isEqualTo(1);
    }

   
}
