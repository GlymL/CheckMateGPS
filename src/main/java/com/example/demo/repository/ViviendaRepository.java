package com.example.demo.repository;
import com.example.demo.model.Vivienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {
    
    // Este método comprueba si ya existe una vivienda con ese nombre exacto
    boolean existsByNombre(String nombre);
}