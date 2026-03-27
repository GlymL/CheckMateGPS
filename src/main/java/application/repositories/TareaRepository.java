package application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.entities.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
}