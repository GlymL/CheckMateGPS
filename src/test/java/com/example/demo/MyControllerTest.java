
package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.Vivienda;
import com.example.demo.repository.ViviendaRepository;

@SpringBootTest
@AutoConfigureMockMvc 
class MyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViviendaRepository viviendaRepository;

   
    @BeforeEach
    void setUp() {
        viviendaRepository.deleteAll();
    }

    // =========================================================================
    // CM1-1 y CM1-2: Creación exitosa (con y sin foto opcional)
    // =========================================================================

    @Test
    void CM1_1_CrearVivienda_ConTodosLosCampos_Exito() throws Exception {
     
        MockMultipartFile foto = new MockMultipartFile(
                "foto", "micasa.png", MediaType.IMAGE_PNG_VALUE, "contenido ficticio".getBytes());

        mockMvc.perform(multipart("/crear-vivienda")
                        .file(foto)
                        .param("nombre", "Villa Sol")
                        .param("descripcion", "Una casa muy bonita, con vistas al mar."))
                .andExpect(status().isOk()) 
                .andExpect(content().string("¡Vivienda 'Villa Sol' registrada con éxito!"));

        // Verificamos que realmente se guardó en la base de datos
        assertEquals(1, viviendaRepository.count());
    }

    @Test
    void CM1_1_CrearVivienda_SinFotoOpcional_Exito() throws Exception {
        mockMvc.perform(multipart("/crear-vivienda")
                     
                        .param("nombre", "Atico Centro")
                        .param("descripcion", "Atico en el centro de la ciudad."))
                .andExpect(status().isOk());

        assertEquals(1, viviendaRepository.count());
    }

    // =========================================================================
    // CM1-3: Faltan datos obligatorios o formato inválido
    // =========================================================================

    @Test
    void CM1_3_CrearVivienda_FaltaNombre_Error() throws Exception {
        mockMvc.perform(multipart("/crear-vivienda")
                       
                        .param("descripcion", "Casa de prueba."))
                .andExpect(status().isBadRequest()) // Esperamos HTTP 400
                .andExpect(content().string(org.hamcrest.Matchers.containsString("El nombre de la vivienda es obligatorio.")));
        
        assertEquals(0, viviendaRepository.count()); 
    }

    @Test
    void CM1_3_CrearVivienda_FormatoNombreInvalido_Error() throws Exception {
        mockMvc.perform(multipart("/crear-vivienda")
                        .param("nombre", "Casa@Invalida!") 
                        .param("descripcion", "Casa de prueba."))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("El nombre solo puede contener letras, números y espacios.")));
    }

    @Test
    void CM1_3_CrearVivienda_FormatoFotoInvalido_Error() throws Exception {
      
        MockMultipartFile fotoMala = new MockMultipartFile(
                "foto", "documento.pdf", MediaType.APPLICATION_PDF_VALUE, "contenido".getBytes());

        mockMvc.perform(multipart("/crear-vivienda")
                        .file(fotoMala)
                        .param("nombre", "Casa Test")
                        .param("descripcion", "Casa de prueba."))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("La foto debe ser en formato .png o .jpeg.")));
    }

    // =========================================================================
    // CM1-4: Restricción de nombre único
    // =========================================================================

    @Test
    void CM1_4_CrearVivienda_NombreDuplicado_Error() throws Exception {
       
        Vivienda viviendaExistente = new Vivienda();
        viviendaExistente.setNombre("Casa Duplicada");
        viviendaExistente.setDescripcion("La primera casa.");
        viviendaRepository.save(viviendaExistente);

       
        mockMvc.perform(multipart("/crear-vivienda")
                        .param("nombre", "Casa Duplicada")
                        .param("descripcion", "Otra casa distinta pero con igual nombre."))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La operación ha fallado y la vivienda no ha sido creada ya que el nombre debe ser único."));

      
        assertEquals(1, viviendaRepository.count());
    }
}