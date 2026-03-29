package application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.entities.Tarea;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByViviendaIdAndRoommateIdIsNull(String viviendaId);

    List<Tarea> findByViviendaId(String viviendaId);
}

