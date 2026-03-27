package application.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import application.entities.Vivienda;

public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {
    // Hemos cambiado findByHouseName por findByName
    Optional<Vivienda> findByName(String name);
}