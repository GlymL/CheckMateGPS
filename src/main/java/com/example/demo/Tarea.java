package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean completada;

    @ManyToOne
    @JoinColumn(name = "roommate_id")
    private Roommate roommate;

    @ManyToOne
    @JoinColumn(name = "completed_by_id")
    private Roommate completedBy;

    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Vivienda vivienda;

    
    public Tarea() {}

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
    }

    //CM4
    public boolean isCompletada() {
    return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public Roommate getRoommate() {
        return roommate;
    }

    public void setRoommate(Roommate roommate) {
        this.roommate = roommate;
    }

    public Roommate getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Roommate completedBy) {
        this.completedBy = completedBy;
    }

}