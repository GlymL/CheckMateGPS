package com.example.demo;

import java.util.Random;

public class Vivienda {
    private String nombre;
    private String descripcion;
    private String nombreFoto;
    private int idVivienda;

    // Fíjate que el ID ya no se pide en el constructor
    public Vivienda(String nombre, String descripcion, String nombreFoto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nombreFoto = nombreFoto;
        
        // Generamos un ID automático aleatorio (simulando el auto-increment de SQL)
        this.idVivienda = new Random().nextInt(10000) + 1;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNombreFoto() { return nombreFoto; }
    public void setNombreFoto(String nombreFoto) { this.nombreFoto = nombreFoto; }

    public int getId() { return idVivienda; }
    public void setId(int id) { this.idVivienda = id; }

    public void guardarSimulacionBD() {
        System.out.println("==== SIMULACIÓN DE BASE DE DATOS ====");
        System.out.println("ID Generado automáticamente: " + this.idVivienda);
        System.out.println("Ejecutando INSERT INTO viviendas (id, nombre, descripcion, foto) VALUES (" 
                + this.idVivienda + ", '" + this.nombre + "', '" + this.descripcion + "', '" + this.nombreFoto + "');");
        System.out.println("=====================================");
    }
}