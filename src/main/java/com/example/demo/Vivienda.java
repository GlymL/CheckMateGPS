package com.example.demo;

public class Vivienda {
    private String nombre;

    public Vivienda(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Este es el método para "contentar" a tu compañero mañana
    public void guardarSimulacionBD() {
        // En el futuro, aquí el controlador llamará a un ViviendaRepository.
        // Por ahora, simulamos por consola que hace algo.
        System.out.println("==== SIMULACIÓN DE BASE DE DATOS ====");
        System.out.println("Ejecutando INSERT INTO viviendas (nombre) VALUES ('" + this.nombre + "');");
        System.out.println("=====================================");
    }
}
