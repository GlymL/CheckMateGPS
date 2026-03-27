package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
}