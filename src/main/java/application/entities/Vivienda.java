package application.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity 
public class Vivienda {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) 
    private String name;
    
    private String description;
    
    @Lob 
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.BINARY)
    private byte[] image;

    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.ALL)
    private List<Roommate> roommates = new ArrayList<>();

    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.ALL)
    private List<Tarea> tareas = new ArrayList<>();

    // 5. CONSTRUCTOR VACÍO OBLIGATORIO PARA JPA (La base de datos lo necesita para funcionar)
    public Vivienda() {
    }

    public Vivienda(String name, String desc) {
        validateName(name);
        validateDescription(desc);
        
        this.name = name;
        this.description = desc;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios por rellenar.");
        }
        if (!name.matches("^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$")) {
            throw new IllegalArgumentException("El nombre no cumple el formato. Solo puede contener letras, números y espacios.");
        }
    }

    private void validateDescription(String desc) {
        if (desc == null || desc.trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios por rellenar.");
        }
        if (!desc.matches("^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:'\"()\\-\\n\\r]+$")) {
            throw new IllegalArgumentException("La descripción contiene caracteres no válidos. Revisa el formato.");
        }
    }

    
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<Roommate> getRoommates() {
        return roommates;
    }

    public void setRoommates(List<Roommate> roommates) {
        this.roommates = roommates;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
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