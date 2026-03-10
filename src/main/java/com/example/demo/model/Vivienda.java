package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Vivienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String direccion;

    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.ALL)
    private List<Roommate> roommates;

    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}