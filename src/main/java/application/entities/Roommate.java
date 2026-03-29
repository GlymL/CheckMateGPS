package application.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Roommate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreUsuario;
    private String nombreReal;

    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private Vivienda vivienda;

    @OneToMany(mappedBy = "asignadoA")
    private List<Tarea> assignedTasks; // Las tareas del front

    public Roommate() {}

    public Roommate(String nombreUsuario, String nombreReal, Vivienda vivienda) {
        this.nombreUsuario = nombreUsuario;
        this.nombreReal = nombreReal;
        this.vivienda = vivienda;
    }

    public List<Tarea> getAssignedTasks() { return assignedTasks; }
    public void setAssignedTasks(List<Tarea> assignedTasks) { this.assignedTasks = assignedTasks; }

    public String getNombreReal() { return nombreReal; }
    public Vivienda getVivienda() { return vivienda; }
    public String getNombreUsuario() { return nombreUsuario; }

    
    public String getName() {
        return this.nombreReal;
    }
}