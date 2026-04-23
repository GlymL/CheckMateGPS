package com.UNIT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import application.entities.Vivienda;

class ViviendaTest {

    @Test
    void validateImage_createViviendaCorrectlyWithoutImage() {

        // 1. Arrange
        String nombreValido = "Piso Estudiantes 1";
        String descValida = "Piso muy luminoso, exterior.";
   
        // 2. Act
        Vivienda vivienda = new Vivienda(nombreValido, descValida);
       
        // 3. Assert 
        assertThat(vivienda.getName()).isEqualTo(nombreValido);
        assertThat(vivienda.getDescription()).isEqualTo(descValida);
        assertThat(vivienda.getImage()).isNull();
    }

    @Test
    void validarFoto_createViviendaCorrectlyWithImagePNG() {

        // 1. Arrange 
        Vivienda vivienda = new Vivienda("Chalet UCM", "Chalet con piscina.");
        byte[] fotoBytes = new byte[]{1, 2, 3, 4}; // Simulamos datos de una imagen
       
        // 2. Act
        vivienda.setImage(fotoBytes);
     
        // 3. Assert
        assertThat(vivienda.getImage()).containsExactly(1, 2, 3, 4);
    }

    @Test
    void validateName_throwExceptionForEmptyName() {

        // 1. Arrange 
        String nombreInvalido = "   "; 
        String descValida = "Descripción correcta.";
     
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreInvalido, descValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Faltan datos obligatorios");
    }

    @Test
    void validateName_throwsExceptionDueToIncorrectNameFormat() {

        // 1. Arrange 
        String nombreInvalido = "Nombre incorrecto@!"; 
        String descValida = "Descripción correcta.";
        
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreInvalido, descValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no cumple el formato");
    }


    @Test
    void equals_returnsTrueIfTheyHaveTheSameName() {

        // 1. Arrange 
        Vivienda casa1 = new Vivienda("Mi Casa", "Desc 1");
        Vivienda casa2 = new Vivienda("Mi Casa", "Desc distinta");
        
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
     
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreValido, descInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Faltan datos obligatorios");
    }

    @Test
    void validateDescription_ThrowsExceptionDuetoIncorrectDescriptionFormat() {

        // 1. Arrange 
        String nombreValido = "Nombre correcto";
        String descInvalida = "Descripción incorrecta < > { }"; 
        
        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> new Vivienda(nombreValido, descInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La descripción contiene caracteres no válidos");
    }

    @Test
    void hashCode_generateDifferentHashForDifferentDwellings() {

        // 1. Arrange
        Vivienda casa1 = new Vivienda("Casa Madrid", "Desc");
        Vivienda casa2 = new Vivienda("Casa Barcelona", "Desc");
        
        // 2. Act
        int hash1 = casa1.hashCode();
        int hash2 = casa2.hashCode();
        
        // 3. Assert
        assertThat(hash1).isNotEqualTo(hash2);
    }

    
}