package com.example.demo;

import com.example.demo.model.Vivienda;
import com.example.demo.repository.ViviendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MyController {

    private final ViviendaRepository viviendaRepository;

    @Autowired
    public MyController(ViviendaRepository viviendaRepository) {
        this.viviendaRepository = viviendaRepository;
    }


  @PostMapping("/crear-vivienda")
    public ResponseEntity<String> crearVivienda(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
          
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        List<String> errores = new ArrayList<>();

     
        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add("El nombre de la vivienda es obligatorio.");
        } else if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9 ]+$")) {
            errores.add("El nombre solo puede contener letras, números y espacios.");
        }

      
        if (descripcion == null || descripcion.trim().isEmpty()) {
            errores.add("La descripción es obligatoria.");
        } else if (!descripcion.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9 \\p{Punct}]+$")) {
            errores.add("La descripción contiene caracteres no válidos.");
        }

     
        String nombreArchivoFoto = null;
        if (foto != null && !foto.isEmpty()) {
            String nombreOriginal = foto.getOriginalFilename();
            if (nombreOriginal != null) {
                String fotoLower = nombreOriginal.toLowerCase();
                if (!fotoLower.endsWith(".png") && !fotoLower.endsWith(".jpeg") && !fotoLower.endsWith(".jpg")) {
                    errores.add("La foto debe ser en formato .png o .jpeg.");
                } else {
                 
                    nombreArchivoFoto = nombreOriginal;
                    
                 
                }
            }
        }

     
        if (!errores.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Errores: " + String.join(" ", errores));
        }

      if (viviendaRepository.existsByNombre(nombre.trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La operación ha fallado y la vivienda no ha sido creada ya que el nombre debe ser único.");
        }
    try {
            Vivienda nuevaVivienda = new Vivienda();
            nuevaVivienda.setNombre(nombre.trim());
            nuevaVivienda.setDescripcion(descripcion.trim());
            nuevaVivienda.setFoto(nombreArchivoFoto); 

            viviendaRepository.save(nuevaVivienda);

            return ResponseEntity.ok("¡Vivienda '" + nombre + "' registrada con éxito!");
            
        } catch (DataIntegrityViolationException e) {
          
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La operación ha fallado y la vivienda no ha sido creada ya que el nombre debe ser único.");
            
        } catch (Exception e) {
          
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor al intentar guardar la vivienda.");
        }
    }
}