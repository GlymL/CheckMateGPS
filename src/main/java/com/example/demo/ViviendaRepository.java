package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {
    // Hemos cambiado findByHouseName por findByName
    Optional<Vivienda> findByName(String name);
}