
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

    // Le decimos que espere recibir el parámetro "nombreCasa" del formulario
    @PostMapping("/submit")
    public String submit(@RequestParam("nombreCasa") String nombreCasa, Model model) {
        
        System.out.println("El usuario ha creado la casa: " + nombreCasa);
        
        // 1. Instanciamos la clase extra simulando que lo preparamos para la Base de Datos
        Vivienda nuevaVivienda = new Vivienda(nombreCasa);
        
        // 2. Le pasamos el nombre guardado en la clase Java a la vista (result.html)
        model.addAttribute("nombreDeLaCasa", nuevaVivienda.getNombre());
        
        return "result";
    }
}