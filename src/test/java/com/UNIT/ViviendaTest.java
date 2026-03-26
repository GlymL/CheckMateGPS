package com.UNIT;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Vivienda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViviendaTest {

    @Test
    void validateImage_createViviendaCorrectlyWithoutImage() {

        // 1. Arrange
        String nombreValido = "Piso Estudiantes 1";
        String descValida = "Piso muy luminoso, exterior.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
   
        // 2. Act
        Vivienda vivienda = new Vivienda(nombreValido, descValida, mockFoto);
       
        // 3. Assert 
        assertThat(vivienda.getName()).isEqualTo(nombreValido);
        assertThat(vivienda.getDescription()).isEqualTo(descValida);
    }

    @Test
    void validarFoto_createViviendaCorrectlyWithImagePNG() {

        // 1. Arrange 
        String nombreValido = "Chalet UCM";
        String descValida = "Chalet con piscina.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(false); 
        when(mockFoto.getContentType()).thenReturn("image/png"); 
       
        // 2. Act
        Vivienda vivienda = new Vivienda(nombreValido, descValida, mockFoto);
     
        // 3. Assert
        assertThat(vivienda.getImage()).isNotNull();
    }

    @Test
    void validateName_throwExceptionForEmptyName() {

        // 1. Arrange 
        String nombreInvalido = "   "; 
        String descValida = "Descripción correcta.";
        MultipartFile mockFoto = mock(MultipartFile.class);
     
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreInvalido, descValida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Faltan datos obligatorios");
    }

    @Test
    void validateName_throwsExceptionDueToIncorrectNameFormat() {

        // 1. Arrange 
        String nombreInvalido = "Nombre incorrecto@!"; 
        String descValida = "Descripción correcta.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreInvalido, descValida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no cumple el formato");
    }

    @Test
    void validateImage_throwsExceptionByIncorrectPhotoFormat() {

        // 1. Arrange 
        String nombreValido = "Nombre correcto";
        String descValida = "Descripción correcta.";
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(false);
        when(mockFoto.getContentType()).thenReturn("application/pdf"); 

        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreValido, descValida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de imagen no válido");
    }

    @Test
    void equals_returnsTrueIfTheyHaveTheSameName() {

        // 1. Arrange 
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        Vivienda casa1 = new Vivienda("Mi Casa", "Desc 1", mockFoto);
        Vivienda casa2 = new Vivienda("Mi Casa", "Desc distinta", mockFoto);
        
        // 2. Act 
        boolean sonIguales = casa1.equals(casa2);
        
        // 3. Assert 
        assertThat(sonIguales).isTrue();
    }

    @Test
    void validateDescription_throwsExceptionForEmptyDescription() {

        // 1. Arrange 
        String nombreValido = "Nombre correcto";
        String descInvalida = "   "; // Descripción vacía o solo espacios
        MultipartFile mockFoto = mock(MultipartFile.class);
     
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreValido, descInvalida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Faltan datos obligatorios");
    }

    @Test
    void validateDescription_ThrowsExceptionDuetoIncorrectDescriptionFormat() {

        // 1. Arrange 
        String nombreValido = "Nombre correcto";
        String descInvalida = "Descripción incorrecta < > { }"; 
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreValido, descInvalida, mockFoto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La descripción contiene caracteres no válidos");
    }

    @Test
    void hashCode_generateDifferentHashForDifferentDwellings() {

        // 1. Arrange
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        Vivienda casa1 = new Vivienda("Casa Madrid", "Desc", mockFoto);
        Vivienda casa2 = new Vivienda("Casa Barcelona", "Desc", mockFoto);
        
        // 2. Act
        int hash1 = casa1.hashCode();
        int hash2 = casa2.hashCode();
        
        // 3. Assert
        assertThat(hash1).isNotEqualTo(hash2);
    }
}