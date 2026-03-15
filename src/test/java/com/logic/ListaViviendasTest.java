package com.logic;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.ListaViviendasImp;
import com.example.demo.Vivienda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListaViviendasTest {

    @Test
    void InsertVivienda_añadeCasaNuevaYDevuelveSize() throws Exception {
        // Arrange
        ListaViviendasImp lista = new ListaViviendasImp(); // Lista limpia
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        Vivienda nuevaCasa = new Vivienda("Piso Nuevo", "Desc", mockFoto);

        // Act
        int tamañoLista = lista.InsertVivienda(nuevaCasa);

        // Assert
        assertThat(tamañoLista).isEqualTo(1);
        assertThat(lista.getViviendas()).contains(nuevaCasa);
    }

    @Test
    void InsertVivienda_lanzaExcepcionSiCasaYaExiste() throws Exception {
        // Arrange
        ListaViviendasImp lista = new ListaViviendasImp();
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        
        Vivienda casaOriginal = new Vivienda("Casa Duplicada", "Desc 1", mockFoto);
        Vivienda casaCopia = new Vivienda("Casa Duplicada", "Desc 2", mockFoto);
        
        lista.InsertVivienda(casaOriginal); // Metemos la primera

        // Act & Assert
        // Intentamos meter la segunda con el mismo nombre y comprobamos que explota
        assertThatThrownBy(() -> lista.InsertVivienda(casaCopia))
                .isInstanceOf(Exception.class); 
                // Nota: Si creasteis una excepción propia llamada "Existing", 
                // cambiad "Exception.class" por "Existing.class"
    }
}