package application.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import application.entities.Roommate;
import application.entities.Vivienda;

public interface RoommateRepository extends JpaRepository<Roommate, Long> {
    boolean existsByNombreRealAndVivienda(String nombreReal, Vivienda vivienda);
}