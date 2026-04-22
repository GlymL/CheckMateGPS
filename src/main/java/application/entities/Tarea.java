package application.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
@Entity
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String descripcion;
    //fallaba aquí, faltaba la columna completada y por eso no guardaba en la bbdd
    @Column(name = "completada", nullable = false)
    private boolean completada = false;

    private LocalDate fechaRealizacion;

    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Vivienda vivienda;

   // @Column(nullable = false)
   // private Boolean completada = false; // <-- added, default false

    @ManyToOne
    @JoinColumn(name = "roommate_id")
    private Roommate asignadoA;

    public Tarea() {}

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
    }
    public boolean getCompletada() {
      return this.completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public Roommate getAsignadoA() {
        return asignadoA;
    }

    public void setAsignadoA(Roommate asignadoA) {
        this.asignadoA = asignadoA;
    }

    public LocalDate getFechaRealizacion() { 
        return fechaRealizacion; 
    }
    
    public void setFechaRealizacion(LocalDate fechaRealizacion) { 
        this.fechaRealizacion = fechaRealizacion; 
    }
}