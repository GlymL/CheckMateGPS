package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MyController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/submit")
    public String submitHouse(
            @RequestParam String houseName,
            @RequestParam String description,
            @RequestParam MultipartFile image,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Al hacer el "new Vivienda", si hay algún error de formato, 
            // saltará directamente al primer "catch" (IllegalArgumentException)
            Vivienda nuevaVivienda = new Vivienda(houseName, description, image);
            
            // 2. Si el formato es correcto, intentamos meterlo en la lista.
            // Si el nombre ya existe, saltará al segundo "catch" (Exception general)
            ListaViviendas.getInstance().InsertVivienda(nuevaVivienda);
            
            // 3. Si todo va bien, avanzamos al resultado (CM1-2)
            return "redirect:/result";
            
        } catch (IllegalArgumentException e) {
            // Atrapa los errores de los métodos validarNombre, validarDescripcion y validarFoto (CM1-3)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
            
        } catch (Exception e) {
            // Atrapa el error de la lista cuando la vivienda ya existe
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El nombre de una casa no puede existir ya, por favor, introduzca uno nuevo.");
            return "redirect:/";
        }
    }

    @GetMapping("/result")
    public String resultPage() {
        return "result";
    }

    @GetMapping("/listar")
    public String listarViviendas(Model model) {
        try {
            model.addAttribute("listaCasas", ListaViviendas.getInstance().getViviendas());
        } catch (Exception e) {
            // Si el mapa está vacío o da error, no pasa nada
        }
        
        return "listarViviendas"; 
    }
}