package com.example.demo.model;

import jakarta.validation.constraints.*;

public class Vivienda {

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    
    private String descripcion;

    private String foto;
    
    public Vivienda() {}

    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    
}
