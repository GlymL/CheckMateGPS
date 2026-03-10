package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyController {

    @GetMapping("/")
    public String cargarInicio() {
        return "index";
    }

    @PostMapping("/registrar")
    public String procesarRegistro(@RequestParam("nombreVivienda") String nombreInput, Model modelo) {

        // Usamos la clase Vivienda de tu amigo
        Vivienda nuevaVivienda = new Vivienda(nombreInput);

        // Simulamos la BD
        nuevaVivienda.ejecutarMockDB();

        // Pasamos la variable a la vista
        modelo.addAttribute("tituloConfirmado", nuevaVivienda.getTitulo());

        return "result";
    }
}