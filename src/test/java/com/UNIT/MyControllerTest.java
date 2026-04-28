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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.controller.MyController;
import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;
class MyControllerTest {
        @Mock
        private ViviendaRepository viviendaRepository;

        @Mock
        private RoommateRepository roommateRepository;

        @Mock
        private TareaRepository tareaRepository;

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
    void submitHouse_errorWhenNameIsDuplicated_CM1_4() throws Exception {
        // 1. Arrange
        // simulando que el nombre ya existe en la BD.
        when(viviendaRepository.save(any(Vivienda.class)))
                .thenThrow(new RuntimeException("Nombre duplicado"));

        MockMultipartFile mockFoto = new MockMultipartFile("image", "", "image/png", new byte[0]);

        // 2. Act
        MvcResult result = mockMvc.perform(multipart("/submit")
            .file(mockFoto)
            .param("houseName", "Casa Repetida")
            .param("description", "Descripción válida"))
            .andReturn();

        // 3. Assert
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/");
        // Verificamos que el mensaje de error de la CM1-4 esté en el Flash Attributes
        assertThat(result.getFlashMap().get("errorMessage"))
            .isEqualTo("El nombre de una vivienda no puede existir ya, por favor, introduzca uno nuevo.");
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

        
    // CM11-1
    @Test
    void verDescripcionTarea_withDescription_success() throws Exception {
        // Arrange
        Vivienda vivienda = new Vivienda();
        vivienda.setId(10L); 

        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setName("Limpiar el baño");
        tarea.setDescripcion("Usar lejía y limpiar los espejos a fondo.");
        tarea.setVivienda(vivienda);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        // Act & Assert
        mockMvc.perform(get("/tarea/1/ver-descripcion"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("descripcion"))
                .andExpect(model().attributeExists("tarea"))
                .andExpect(model().attributeExists("viviendaId"))
                .andExpect(model().attribute("viviendaId", 10L));
        
        verify(tareaRepository).findById(1L);
    }

    // CM11-2
    @Test
    void verDescripcionTarea_withoutDescription_success() throws Exception {
        // Arrange
        Vivienda vivienda = new Vivienda();
        vivienda.setId(10L);

        Tarea tarea = new Tarea();
        tarea.setId(2L);
        tarea.setName("Bajar la basura");
        tarea.setDescripcion(null); // Sin descripción
        tarea.setVivienda(vivienda);

        when(tareaRepository.findById(2L)).thenReturn(Optional.of(tarea));

        // Act & Assert
        mockMvc.perform(get("/tarea/2/ver-descripcion"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("descripcion")) 
                .andExpect(model().attributeExists("tarea"))
                .andExpect(model().attributeExists("viviendaId"))
                .andExpect(model().attribute("viviendaId", 10L));
                
        verify(tareaRepository).findById(2L);
    }

    //Tarea no encontrada
    @Test
    void verDescripcionTarea_tareaDoesNotExist_redirectsToListar() throws Exception {
        // Arrange
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tarea/99/ver-descripcion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listar"));
                
        verify(tareaRepository).findById(99L);
    } 
}