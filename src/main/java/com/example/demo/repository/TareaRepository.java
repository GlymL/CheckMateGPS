package com.example.demo.repository;
import com.example.demo.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends  JpaRepository<Tarea, Long>{

}
