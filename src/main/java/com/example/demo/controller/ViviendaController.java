package com.example.demo.controller;

import com.example.demo.model.Vivienda;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vivienda")
public class ViviendaController {

    private static List<Vivienda> listaViviendas = new ArrayList<>();

    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vivienda", new Vivienda());
        return "crear-vivienda";
    }

    @PostMapping("/guardar")
    public String guardarVivienda(@Valid @ModelAttribute("vivienda") Vivienda vivienda, 
                                 BindingResult result, Model model) {
        
        
        boolean existe = listaViviendas.stream()
                .anyMatch(v -> v.getNombre().equalsIgnoreCase(vivienda.getNombre()));
        
        if (existe) {
            result.rejectValue("nombre", "error.vivienda", "La vivienda ya existe (el nombre debe ser único)");
        }

        if (result.hasErrors()) {
            return "crear-vivienda";
        }

    
        listaViviendas.add(vivienda);
        return "redirect:/vivienda/exito";
    }

    @GetMapping("/exito")
    @ResponseBody
    public String exito() {
        return "¡Vivienda creada con éxito! Ahora puedes gestionar tus tareas.";
    }
}
