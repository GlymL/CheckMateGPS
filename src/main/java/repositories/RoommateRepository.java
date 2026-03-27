package repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import entities.Roommate;
import entities.Vivienda;

public interface RoommateRepository extends JpaRepository<Roommate, Long> {
    boolean existsByNombreRealAndVivienda(String nombreReal, Vivienda vivienda);
}