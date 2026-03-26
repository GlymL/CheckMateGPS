package com.example.demo;

import java.util.Optional; // NECESARIO PARA LOS REPOSITORIOS

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        } catch (Exception e) {
            
        }
        return "listarViviendas"; 
    }
  
    //cosas de añadir Roommate

    // 1. Muestra el formulario vacío
    @GetMapping("/add-roommate")
    public String mostrarFormularioRoommate() {
        return "addRoommate"; 
    }

    // 2. Recibe los datos del formulario y aplica tus reglas estrictas
   @PostMapping("/add-roommate")
public String procesarAñadirRoommate(
        @RequestParam(value = "nombreVivienda", required = false, defaultValue = "") String nombreVivienda,
        @RequestParam(value = "nombreUsuario", required = false, defaultValue = "") String nombreUsuario,
        @RequestParam(value = "nombre", required = false, defaultValue = "") String nombreReal,
        RedirectAttributes redirectAttributes) {

        // Validar que no vengan vacíos (CM2-3)
        if (nombreVivienda.isBlank() || nombreUsuario.isBlank() || nombreReal.isBlank()) {
            redirectAttributes.addFlashAttribute("errorCampos", true);
            return "redirect:/add-roommate";
        }

        // Validar el formato del nombre real (CM2-4)
       if (!nombreReal.matches("^[a-zA-Z\\s]+$")) {
        redirectAttributes.addFlashAttribute("errorFormato", true);
         return "redirect:/add-roommate";
        }

        // Comprobar que la vivienda existe
       Optional<Vivienda> viviendaOpt = viviendaRepository.findByName(nombreVivienda);
        if (viviendaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorVivienda", true);
            return "redirect:/add-roommate";
        }
        
        Vivienda viviendaEncontrada = viviendaOpt.get();

        // Comprobar si ya existe el roommate en esa casa (CM2-5)
        boolean existeDuplicado = roommateRepository.existsByNombreRealAndVivienda(nombreReal, viviendaEncontrada);
        if (existeDuplicado) {
            redirectAttributes.addFlashAttribute("errorDuplicado", true);
            return "redirect:/add-roommate";
        }

        // Crear y guardar (CM2-2)
        Roommate nuevoRoommate = new Roommate(nombreUsuario, nombreReal, viviendaEncontrada);
        roommateRepository.save(nuevoRoommate);

        return "redirect:/listar";
    }

    @PostMapping("/guardarTarea")
    public String guardarTareaManual(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("viviendaId") Long viviendaId,
            Model model) {

        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("error", "No se han rellenado todos los campos obligatorios.");
            return "crearTarea";
        }

        String regexNombre = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$";
        String regexDescripcion = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:()]*$"; 

        if (!nombre.matches(regexNombre)) {
            model.addAttribute("error", "El formato del nombre no es válido.");
            return "crearTarea";
        }

        if (descripcion != null && !descripcion.isEmpty() && !descripcion.matches(regexDescripcion)) {
            model.addAttribute("error", "El formato de la descripción no es válido.");
            return "crearTarea";
        }

        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(viviendaId);
        
        if (viviendaOpt.isPresent()) {
            Tarea nuevaTarea = new Tarea();
            nuevaTarea.setNombre(nombre);
            nuevaTarea.setDescripcion(descripcion);
            nuevaTarea.setVivienda(viviendaOpt.get());

            tareaRepository.save(nuevaTarea);
            // TODO: Cambiar este redirect en el futuro para que lleve a la vista de tareas
            // de la vivienda actual (ej: /vivienda/tareas?id=X) en lugar de la lista general.
            return "redirect:/listar"; 
        } else {
            model.addAttribute("error", "Vivienda no encontrada.");
            return "crearTarea";
        }
    }
}