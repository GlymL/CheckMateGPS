package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoommateRepository extends JpaRepository<Roommate, Long> {
    boolean existsByNombreRealAndVivienda(String nombreReal, Vivienda vivienda);
}