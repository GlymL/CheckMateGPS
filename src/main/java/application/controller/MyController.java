package application.controller;

import java.util.List;
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
  // 1. Buscar la vivienda 
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(id);
        
        // 2. Comprobar si la vivienda existe
        if (viviendaOpt.isPresent()) {
            Vivienda vivienda = viviendaOpt.get(); // Extraemos el objeto real
            
          
            model.addAttribute("viviendaNombre", vivienda.getName()); 
           
            model.addAttribute("viviendaId", id); 
            
            return "detalleVivienda"; 
        } else {
            // Si el ID no existe, lo devolvemos a la lista 
            return "redirect:/listar";
        }
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
            @RequestParam("name") String name,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("viviendaId") Long viviendaId,
            Model model) {
        
        // 1. Buscar la vivienda 
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(viviendaId);
        if (viviendaOpt.isEmpty()) {
            return "redirect:/listar";
        }
        Vivienda vivienda = viviendaOpt.get();

        // 2. Preparar la tarea  
        Tarea tareaTemporal = new Tarea();
        tareaTemporal.setName(name); 
        tareaTemporal.setDescripcion(descripcion);
        tareaTemporal.setVivienda(vivienda); 

        // 3. Validaciones
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "No se han rellenado todos los campos obligatorios.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            return "crearTarea";
        }

        String regexNombre = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$";
        String regexDescripcion = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:()]*$"; 

        if (!name.matches(regexNombre)) {
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

        // 4. Si todo está correcto, guarda la tarea 
        tareaRepository.save(tareaTemporal); 
        
        return "redirect:/vivienda/" + viviendaId + "/listTareas"; 
    }

  
  
@GetMapping("/asignarTarea")
public String mostrarPantallaAsignar(Model model) {
    
   
    List<Tarea> listaTareas = tareaRepository.findAll(); 
    List<Roommate> listaRoommates = roommateRepository.findAll();

    
    model.addAttribute("tareas", listaTareas);
    model.addAttribute("roommates", listaRoommates);

   
    return "asignarTarea"; 
}

  
    

    @GetMapping("/vivienda/{id}/listTareas")
    public String viewAssignedTareas(@PathVariable("id") Long id, Model model) {
        
        java.util.List<Roommate> listaDeRoommates = roommateRepository.findByViviendaId(id);

        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(id);

        if (viviendaOpt.isPresent()) {
            model.addAttribute("roommates", listaDeRoommates); 
            model.addAttribute("viviendaId", id);
            model.addAttribute("vivienda", viviendaOpt.get());

            return "listTareas";
        } else {
            return "redirect:/listar";
        }
    }
}