package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam("nombre") String nombreRecibido, Model model) {
        
        // 1. Instanciamos la clase con el nombre que escribió el usuario
        Vivienda nuevaVivienda = new Vivienda(nombreRecibido);
        
        // 2. Simulamos la subida a la base de datos
        nuevaVivienda.guardarSimulacionBD();
        
        // 3. Pasamos el nombre a la pantalla de éxito
        model.addAttribute("nombreCasa", nuevaVivienda.getNombre());
        
        return "result";
    }
}