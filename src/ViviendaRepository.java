package com.tuapp.repositories;

import com.tuapp.models.Vivienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {
    boolean existsByNombre(String nombre);
}