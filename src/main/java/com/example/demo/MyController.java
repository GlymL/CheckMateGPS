package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.io.IOException;

@Controller
public class MyController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Ojo al "throws IOException" que hemos añadido aquí
    @PostMapping("/submit")
    public String submit(
            @RequestParam("nombre") String nombreRecibido, 
            @RequestParam("descripcion") String descripcionRecibida, 
            @RequestParam("foto") MultipartFile fotoRecibida, 
            Model model) throws IOException { 
        
        String nombreArchivo = fotoRecibida.getOriginalFilename();

        // TRUCO PRO: Convertimos los bytes de la foto a un formato de texto (Base64)
        String imagenBase64 = "";
        if (fotoRecibida != null && !fotoRecibida.isEmpty()) {
            byte[] bytesFoto = fotoRecibida.getBytes();
            imagenBase64 = Base64.getEncoder().encodeToString(bytesFoto);
        }

        Vivienda nuevaVivienda = new Vivienda(nombreRecibido, descripcionRecibida, nombreArchivo);
        nuevaVivienda.guardarSimulacionBD();
        
        model.addAttribute("nombreCasa", nuevaVivienda.getNombre());
        model.addAttribute("descCasa", nuevaVivienda.getDescripcion());
        
        // Pasamos la foto codificada al HTML
        model.addAttribute("fotoBase64", imagenBase64);
        
        return "result";
    }
}