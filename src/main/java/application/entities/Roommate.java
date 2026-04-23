package application.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Roommate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   // private String nombreUsuario; // CM2-3
    private String nombreReal;    // CM2-1, CM2-2, CM2-4

    // Relación: Muchos roommates pertenecen a una vivienda
    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Vivienda vivienda;

    // Tareas asignadas a este roommate
    @OneToMany(mappedBy = "asignadoA")
    private java.util.List<Tarea> assignedTasks;

    public java.util.List<Tarea> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(java.util.List<Tarea> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    // Constructores vacíos obligatorios para la base de datos
    public Roommate() {}

    public Roommate( String nombreReal, Vivienda vivienda) {
       // this.nombreUsuario = nombreUsuario;
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
   

}