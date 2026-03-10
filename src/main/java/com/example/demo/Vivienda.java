package com.example.demo;

public class Vivienda {
    private String titulo;

    public Vivienda(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // El mock de la base de datos con un formato de consola distinto
    public void ejecutarMockDB() {
        System.out.println(">>> [SISTEMA] Conectando al motor de base de datos...");
        System.out.println(">>> [SQL] INSERT INTO tabla_viviendaes (titulo_vivienda) VALUES ('" + this.titulo + "');");
        System.out.println(">>> [SISTEMA] Registro completado con éxito.");
    }
}