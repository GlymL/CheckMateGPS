package com.logic;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Vivienda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViviendaTest {

    @Test
    void constructor_creaViviendaCorrectamenteSinFoto() {

        String nombreValido = "Piso Estudiantes 1";
        String descValida = "Piso muy luminoso, exterior.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);

   
        Vivienda vivienda = new Vivienda(nombreValido, descValida, mockFoto);

       
        assertThat(vivienda.getName()).isEqualTo(nombreValido);
        assertThat(vivienda.getDescription()).isEqualTo(descValida);
    }

    @Test
    void constructor_creaViviendaCorrectamenteConFotoPng() {
     
        String nombreValido = "Chalet UCM";
        String descValida = "Chalet con piscina.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(false); 
        when(mockFoto.getContentType()).thenReturn("image/png"); 

       
        Vivienda vivienda = new Vivienda(nombreValido, descValida, mockFoto);

     
        assertThat(vivienda.getImage()).isNotNull();
    }

    @Test
    void constructor_lanzaExcepcionPorNombreVacio() {
       
        String nombreInvalido = "   "; 
        String descValida = "Descripción correcta.";
        MultipartFile mockFoto = mock(MultipartFile.class);

     
        assertThatThrownBy(() -> new Vivienda(nombreInvalido, descValida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Faltan datos obligatorios");
    }

    @Test
    void constructor_lanzaExcepcionPorFormatoNombreIncorrecto() {
 
        String nombreInvalido = "Piso_Universitario@!"; 
        String descValida = "Descripción correcta.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);

        
        assertThatThrownBy(() -> new Vivienda(nombreInvalido, descValida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no cumple el formato");
    }

    @Test
    void constructor_lanzaExcepcionPorFormatoFotoIncorrecto() {
      
        String nombreValido = "Piso Normal";
        String descValida = "Descripción correcta.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(false);
        when(mockFoto.getContentType()).thenReturn("application/pdf"); 

        assertThatThrownBy(() -> new Vivienda(nombreValido, descValida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de imagen no válido");
    }

    @Test
    void equals_devuelveTrueSiTienenMismoNombre() {
       
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        Vivienda casa1 = new Vivienda("Mi Casa", "Desc 1", mockFoto);
        Vivienda casa2 = new Vivienda("Mi Casa", "Desc distinta", mockFoto);

        
        boolean sonIguales = casa1.equals(casa2);

        
        assertThat(sonIguales).isTrue();
    }
}
