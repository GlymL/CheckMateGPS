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
        
        // 1. Arrange 
        ListaViviendasImp lista = new ListaViviendasImp();
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        Vivienda nuevaCasa = new Vivienda("Piso Nuevo", "Desc", mockFoto);

        // 2. Act 
        int tamañoLista = lista.InsertVivienda(nuevaCasa);

        // 3. Assert 
        assertThat(tamañoLista).isEqualTo(1);
        assertThat(lista.getViviendas()).contains(nuevaCasa);
    }

    @Test
    void InsertVivienda_lanzaExcepcionSiCasaYaExiste() throws Exception {

        // 1. Arrange 
        ListaViviendasImp lista = new ListaViviendasImp();
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        
        Vivienda casaOriginal = new Vivienda("Casa Duplicada", "Desc 1", mockFoto);
        Vivienda casaCopia = new Vivienda("Casa Duplicada", "Desc 2", mockFoto);
        
        lista.InsertVivienda(casaOriginal); 

        // 2 y 3. Act y Assert
        assertThatThrownBy(() -> lista.InsertVivienda(casaCopia))
                .isInstanceOf(Exception.class); 
    }

    @Test
    void InsertVivienda_lanzaExcepcionYNoAlteraTamanoSiCasaYaExiste() throws Exception {

        // 1. Arrange
        ListaViviendasImp lista = new ListaViviendasImp();
        MultipartFile mockFoto = mock(MultipartFile.class);
        when(mockFoto.isEmpty()).thenReturn(true);
        
        Vivienda casaA = new Vivienda("Casa A", "Descripción A", mockFoto);
        Vivienda casaB = new Vivienda("Casa B", "Descripción B", mockFoto);
        
        lista.InsertVivienda(casaA);
        lista.InsertVivienda(casaB);

        // 2 y 3. Act y Assert 
        assertThatThrownBy(() -> lista.InsertVivienda(casaB))
                .isInstanceOf(Exception.class); 
                
        // 3. Assert 
        assertThat(lista.getViviendas().size()).isEqualTo(2);
    }
}