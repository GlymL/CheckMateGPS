package com.example.demo;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class ViviendaRepository {
    private Map<String, Vivienda> viviendas = new HashMap<>();

    public boolean existsByNombre(String nombre) {
        return viviendas.containsKey(nombre);
    }

    public void save(Vivienda v) {
        viviendas.put(v.getNombre(), v);
    }

    public Collection<Vivienda> findAll() {
        return viviendas.values();
    }
}