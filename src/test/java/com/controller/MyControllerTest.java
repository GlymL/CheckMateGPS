package com.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;


import com.example.demo.MyController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

class MyControllerTest {

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new MyController()).build();

    @Test
    void home_devuelveVistaIndex() throws Exception {

        // 1. Arrange
        // No se necesitan datos previos

        // 2. Act
        var result = mockMvc.perform(get("/"))
                .andReturn();

        // 3. Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("index");
    }

    
   

    @Test
    void listarViviendas_devuelveVistaListarViviendas() throws Exception {

        // 1. Arrange
        // No se necesitan datos

        // 2. Act
        var result = mockMvc.perform(get("/listar"))
                .andReturn();

        // 3. Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getModelAndView().getViewName()).isEqualTo("listarViviendas");
    }

    @Test
    void submitHouse_creaViviendaCorrectamenteSinFoto() throws Exception {

        // 1. Arrange
        MockMultipartFile mockFoto = new MockMultipartFile(
                "image",
                "",
                "image/png",
                new byte[0]
        );

        // 2. Act
        MvcResult result = mockMvc.perform(multipart("/submit")
                .param("houseName", "Casa Test")
                .param("description", "Descripcion correcta")
                .file(mockFoto))
                .andReturn();

        // 3. Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/result");
    }

    @Test
    void submitHouse_lanzaErrorPorNombreVacio() throws Exception {

        // 1. Arrange
        MockMultipartFile mockFoto = new MockMultipartFile(
                "image",
                "",
                "image/png",
                new byte[0]
        );

        // 2. Act
        MvcResult result = mockMvc.perform(multipart("/submit")
                .param("houseName", "")
                .param("description", "Descripcion correcta")
                .file(mockFoto))
                .andReturn();

        // 3. Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/");
    }

    @Test
    void submitHouse_lanzaErrorPorFormatoFotoIncorrecto() throws Exception {

        // 1. Arrange
        MockMultipartFile mockFoto = new MockMultipartFile(
                "image",
                "foto.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // 2. Act
        MvcResult result = mockMvc.perform(multipart("/submit")
                .param("houseName", "Casa Test")
                .param("description", "Descripcion correcta")
                .file(mockFoto))
                .andReturn();

        // 3. Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/");
    }

}