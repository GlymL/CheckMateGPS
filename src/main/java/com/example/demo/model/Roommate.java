package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Roommate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Vivienda vivienda;

    @OneToMany(mappedBy = "roommate", cascade = CascadeType.ALL)
    private List<Tarea> tareas;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }
}