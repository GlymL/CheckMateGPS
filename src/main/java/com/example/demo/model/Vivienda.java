package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Vivienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  @Column(unique = true, nullable = false) 
    private String nombre;
    private String descripcion;
     @Column( nullable = true) 
    private String foto;
 

    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}