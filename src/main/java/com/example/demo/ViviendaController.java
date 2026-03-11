package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequestMapping("/viviendas")
public class ViviendaController {

    private final ViviendaRepository repo;

    public ViviendaController(ViviendaRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/crear")
    public String crearVivienda(@RequestBody Vivienda v) {
        String error = ViviendaValidator.validate(v);
        if (error != null) return "Error: " + error;

        if (repo.existsByNombre(v.getNombre())) {
            return "Error: Ya existe una vivienda con ese nombre";
        }

        repo.save(v);
        return "Vivienda creada con éxito: " + v.getNombre();
    }

    @GetMapping("/listar")
    public Collection<Vivienda> listarViviendas() {
        return repo.findAll();
    }
}