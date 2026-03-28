package application.controller;

import java.util.Optional; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import application.entities.Roommate;
import application.entities.Tarea;
import application.entities.Vivienda;
import application.repositories.RoommateRepository;
import application.repositories.TareaRepository;
import application.repositories.ViviendaRepository;

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
    public String verVivienda(@PathVariable("id") Long id, Model model) {
        model.addAttribute("viviendaId", id);
        return "detalleVivienda"; 
    }

   @GetMapping("/vivienda/{id}/nueva-tarea")
    public String nuevaTarea(@PathVariable("id") Long id, Model model) {
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(id);
        
        if (viviendaOpt.isEmpty()) {
            return "redirect:/listar"; 
        }
        
        Tarea tarea = new Tarea();
      
        tarea.setVivienda(viviendaOpt.get()); 
        
        model.addAttribute("tarea", tarea);
        model.addAttribute("viviendaId", id); 
        return "crearTarea";
    }
    
  @PostMapping("/guardarTarea")
    public String guardarTarea(
            @RequestParam("nombre") String nombre, // Recibimos "nombre" correctamente
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("vivienda.id") Long viviendaId,
            Model model) {
        
       
        Tarea tareaTemporal = new Tarea();
        tareaTemporal.setNombre(nombre); 
        tareaTemporal.setDescripcion(descripcion);

        // --- VALIDACIONES ---
        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("error", "No se han rellenado todos los campos obligatorios.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            return "crearTarea";
        }

        String regexNombre = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$";
        String regexDescripcion = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:()]*$"; 

        if (!nombre.matches(regexNombre)) {
            model.addAttribute("error", "El formato del nombre no es válido.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            return "crearTarea";
        }

        if (descripcion != null && !descripcion.isEmpty() && !descripcion.matches(regexDescripcion)) {
            model.addAttribute("error", "El formato de la descripción no es válido.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            return "crearTarea";
        }

      
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(viviendaId);
        
        if (viviendaOpt.isPresent()) {
            Tarea nuevaTarea = new Tarea();
            nuevaTarea.setNombre(nombre);
            nuevaTarea.setDescripcion(descripcion);
            nuevaTarea.setVivienda(viviendaOpt.get()); 

            tareaRepository.save(nuevaTarea); 
            
            return "redirect:/listar"; 
        } else {
            model.addAttribute("error", "Vivienda no encontrada.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            return "crearTarea";
        }
    }
    

    @GetMapping("/vivienda/{id}/listTareas")
    public String listTareas(@PathVariable("id") String id, Model model) {
        
        model.addAttribute("roommates", roommateRepository.findByViviendaId(id));
    
        model.addAttribute("viviendaId", id);
        return "listTareas"; 
    }
        

    
}