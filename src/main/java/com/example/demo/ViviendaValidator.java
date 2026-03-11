package com.example.demo;


public class ViviendaValidator {

    public static String validate(Vivienda v) {
        if (v.getNombre() == null || !v.getNombre().matches("[a-zA-Z0-9 ]+")) {
            return "Nombre inválido (solo letras, números y espacios)";
        }
        if (v.getDescripcion() == null || !v.getDescripcion().matches("[\\w\\s.,;:!?()-]+")) {
            return "Descripción inválida";
        }
        if (v.getFoto() != null && !v.getFoto().matches(".+\\.(png|jpeg)$")) {
            return "Foto inválida (solo .png o .jpeg)";
        }
        return null; 
    }
}