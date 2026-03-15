package com.example.demo;

import org.springframework.web.multipart.MultipartFile;

public class Vivienda {

    private String name;
    private String description;
    private MultipartFile image;
    
    public Vivienda(String name, String desc, MultipartFile image) {
        validarNombre(name);
        validarDescripcion(desc);
        validarFoto(image);
        
        this.name = name;
        this.description = desc;
        this.image = image;
    }

    
    private void validarNombre(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios por rellenar.");
        }
        if (!name.matches("^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$")) {
            throw new IllegalArgumentException("El nombre no cumple el formato. Solo puede contener letras, números y espacios.");
        }
    }

    private void validarDescripcion(String desc) {
        if (desc == null || desc.trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios por rellenar.");
        }
        if (!desc.matches("^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:'\"()\\-\\n\\r]+$")) {
            throw new IllegalArgumentException("La descripción contiene caracteres no válidos. Revisa el formato.");
        }
    }

    private void validarFoto(MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Formato de imagen no válido. Solo se admiten archivos .png o .jpeg.");
            }
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vivienda))
            return false;
        Vivienda other = (Vivienda)o;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}