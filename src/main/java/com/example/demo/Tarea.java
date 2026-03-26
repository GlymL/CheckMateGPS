package com.example.demo;

public class Tarea {
    private String name; // Coincide con el formato de tu app
    private String descripcion;
    private String viviendaId;

    // Constructor vacío necesario para Thymeleaf
    public Tarea() {}

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getViviendaId() { return viviendaId; }
    public void setViviendaId(String viviendaId) { this.viviendaId = viviendaId; }
}