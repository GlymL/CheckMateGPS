package com.example.demo;

import jakarta.persistence.*; // Importaciones necesarias para BBDD
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;

@Entity // 1. Indica a Spring que esto será una tabla en la BBDD
public class Vivienda {

    @Id // 2. Indica que este es el identificador único (Clave primaria)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental (1, 2, 3...)
    private Long id;

    @Column(unique = true) // Hace que la BBDD rechace nombres duplicados automáticamente
    private String name;
    
    private String description;
    
    @Transient // 3. Le dice a la BBDD que ignore este campo (JPA no puede guardar un MultipartFile)
    private MultipartFile image;

    // 4. Relación: Una vivienda tiene una lista de roommates
    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.ALL)
    private List<Roommate> roommates = new ArrayList<>();

    // 5. CONSTRUCTOR VACÍO OBLIGATORIO PARA JPA (La base de datos lo necesita para funcionar)
    public Vivienda() {
    }

    // 6. TU CONSTRUCTOR ORIGINAL (¡Intacto! Mantenemos tus excelentes validaciones)
    public Vivienda(String name, String desc, MultipartFile image) {
        validarNombre(name);
        validarDescripcion(desc);
        validarFoto(image);
        
        this.name = name;
        this.description = desc;
        this.image = image;
    }

    // --- TUS MÉTODOS DE VALIDACIÓN ORIGINALES ---
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

    // --- GETTERS Y SETTERS ---
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Roommate> getRoommates() {
        return roommates;
    }

    public void setRoommates(List<Roommate> roommates) {
        this.roommates = roommates;
    }

    // --- EQUALS & HASHCODE ORIGINALES ---
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