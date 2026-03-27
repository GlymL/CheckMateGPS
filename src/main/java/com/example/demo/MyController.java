package com.example.demo;

import java.util.Optional; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MyController {

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private RoommateRepository roommateRepository;
    
    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private TareaService tareaService;

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
            Vivienda nuevaVivienda = new Vivienda(houseName, description, image);
            viviendaRepository.save(nuevaVivienda);
            return "redirect:/result";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
            
        } catch (Exception e) {
           
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
            model.addAttribute("listaCasas", viviendaRepository.findAll());
        } catch (Exception e) {}
        return "listarViviendas"; 
    }
  
    @GetMapping("/add-roommate")
    public String mostrarFormularioRoommate() {
        return "addRoommate"; 
    }

    @PostMapping("/add-roommate")
    public String procesarAñadirRoommate(
        @RequestParam(value = "nombreVivienda", required = false, defaultValue = "") String nombreVivienda,
        @RequestParam(value = "nombreUsuario", required = false, defaultValue = "") String nombreUsuario,
        @RequestParam(value = "nombre", required = false, defaultValue = "") String nombreReal,
        RedirectAttributes redirectAttributes) {

        if (nombreVivienda.isBlank() || nombreUsuario.isBlank() || nombreReal.isBlank()) {
            redirectAttributes.addFlashAttribute("errorCampos", true);
            return "redirect:/add-roommate";
        }

        if (!nombreReal.matches("^[a-zA-Z\\s]+$")) {
            redirectAttributes.addFlashAttribute("errorFormato", true);
            return "redirect:/add-roommate";
        }

        Optional<Vivienda> viviendaOpt = viviendaRepository.findByName(nombreVivienda);
        if (viviendaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorVivienda", true);
            return "redirect:/add-roommate";
        }
        
        Vivienda viviendaEncontrada = viviendaOpt.get();

        boolean existeDuplicado = roommateRepository.existsByNombreRealAndVivienda(nombreReal, viviendaEncontrada);
        if (existeDuplicado) {
            redirectAttributes.addFlashAttribute("errorDuplicado", true);
            return "redirect:/add-roommate";
        }

        Roommate nuevoRoommate = new Roommate(nombreUsuario, nombreReal, viviendaEncontrada);
        roommateRepository.save(nuevoRoommate);

        return "redirect:/listar";
    }

    @GetMapping("/vivienda/{id}")
    public String verVivienda(@PathVariable Long id, Model model) {

        Vivienda vivienda = viviendaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vivienda no encontrada"));

        model.addAttribute("viviendaId", id);
        model.addAttribute("tareas", vivienda.getTareas());

        return "detalleVivienda"; 
    }

    @GetMapping("/vivienda/{id}/nueva-tarea")
    public String nuevaTarea(@PathVariable("id") String id, Model model) {
        Tarea tarea = new Tarea();
        Vivienda v = viviendaRepository.findById(Long.parseLong(id))
            .orElseThrow(() -> new RuntimeException("Vivienda no encontrada"));

        tarea.setVivienda(v);
        
        model.addAttribute("tarea", tarea);
        return "crearTarea";
    }
    
   
    @PostMapping("/guardarTarea")
    public String guardarTarea(@ModelAttribute Tarea tarea) {

        tareaRepository.save(tarea);

        return "redirect:/vivienda/" + tarea.getVivienda().getId();
    }

    @PostMapping("/tareas/{id}/completar")
    public String completarTarea(
            @PathVariable Long id,
            @RequestParam Long roommateId,
            RedirectAttributes redirectAttributes) {

        try {
            tareaService.completarTarea(id, roommateId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        Tarea tarea = tareaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        return "redirect:/vivienda/" + tarea.getVivienda().getId();
    }
}