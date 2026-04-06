package application.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Roommate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreUsuario; // CM2-3
    private String nombreReal;    // CM2-1, CM2-2, CM2-4

    // Relación: Muchos roommates pertenecen a una vivienda
    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Vivienda vivienda;

    // Constructores vacíos obligatorios para la base de datos
    public Roommate() {}

    public Roommate(String nombreUsuario, String nombreReal, Vivienda vivienda) {
        this.nombreUsuario = nombreUsuario;
        this.nombreReal = nombreReal;
        this.vivienda = vivienda;
    }
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombreReal() { return nombreReal; }
    public Vivienda getVivienda() { return vivienda; }
    public String getNombreUsuario() {
    return nombreUsuario;
}

}