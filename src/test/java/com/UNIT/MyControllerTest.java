package com.UNIT;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import controller.MyController;
import entities.Roommate;
import entities.Vivienda;
import repositories.RoommateRepository;
import repositories.ViviendaRepository;
class MyControllerTest {
        @Mock
        private ViviendaRepository viviendaRepository;

        @Mock
        private RoommateRepository roommateRepository;

        @InjectMocks
        private MyController controller;

        private MockMvc mockMvc;
    @BeforeEach
        void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

    @Test
    void home_returnsViewIndex() throws Exception {

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
    void listViviendas_returnsViewListHomes() throws Exception {

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
    void submitHouse_createHousingCorrectlyWithoutPhoto() throws Exception {

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
    void submitHouse_throwsErrorForEmptyName() throws Exception {

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
    void submitHouse_throwsErrorByPhotoFormatIncorrect() throws Exception {

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

    // CM2: Test para verificar los campos para añadir un nuevo roommate 
        // CM2-2: datos correctos → se crea roommate
       @Test
        void addRoommate_createCorrectly() throws Exception {

        Vivienda vivienda = new Vivienda();
        vivienda.setName("Casa1");

        when(viviendaRepository.findByName("Casa1"))
                .thenReturn(Optional.of(vivienda));

        when(roommateRepository.existsByNombreRealAndVivienda("Ana", vivienda))
                .thenReturn(false);

        MvcResult result = mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "Casa1")
                .param("nombreUsuario", "ana123")
                .param("nombre", "Ana"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/listar");

        verify(roommateRepository).save(any(Roommate.class));
        }
        // CM2-3: campos vacíos → error
        @Test
        void addRoommate_empty_fields_error() throws Exception {

        MvcResult result = mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "Casa1")
                .param("nombreUsuario", "")
                .param("nombre", "Ana"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/add-roommate");

        verify(roommateRepository, never()).save(any(Roommate.class));
        }
        // CM2-4: campos vacíos → error
        @Test
        void addRoommate_Invalid_format_error() throws Exception {

        MvcResult result = mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "Casa1")
                .param("nombreUsuario", "ana123")
                .param("nombre", "Ana123"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/add-roommate");

        verify(roommateRepository, never()).save(any(Roommate.class));
        }
    //CM 2-5
    @Test
        void addRoommate_errorWhenDuplicated() throws Exception {
        Vivienda vivienda = new Vivienda();
        vivienda.setName("Casa1");

        when(viviendaRepository.findByName("Casa1"))
                .thenReturn(Optional.of(vivienda));

        when(roommateRepository.existsByNombreRealAndVivienda("Maria", vivienda))
                .thenReturn(true);

        MvcResult result = mockMvc.perform(post("/add-roommate")
                .param("nombreVivienda", "Casa1")
                .param("nombreUsuario", "user1")
                .param("nombre", "Maria"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/add-roommate");

        verify(roommateRepository, never()).save(any(Roommate.class));
        }
}