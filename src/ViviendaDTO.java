

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public class ViviendaDTO {

    @NotBlank(message = "El nombre de la vivienda es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras, números y espacios")
    private String nombre;

    @NotBlank(message = "La descripción de la vivienda es obligatoria")
    private String descripcion;

    // La validación de este campo la haremos en el Service ya que es un archivo
    private MultipartFile foto;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public MultipartFile getFoto() { return foto; }
    public void setFoto(MultipartFile foto) { this.foto = foto; }
}