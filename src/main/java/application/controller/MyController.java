package application.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
                    "El nombre de una vivienda no puede existir ya, por favor, introduzca uno nuevo.");
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
  
    @GetMapping("/vivienda/{id}/nuevo-roommate")
    public String mostrarFormularioRoommate(@PathVariable("id") Long id, Model model) {
        model.addAttribute("viviendaId", id);
        return "addRoommate"; 
    }

    @PostMapping("/add-roommate")
    public String procesarAñadirRoommate(
       @RequestParam("viviendaId") Long viviendaId,
        @RequestParam("nombreReal") String nombreReal, 
        RedirectAttributes redirectAttributes) {

        if (nombreReal.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre no puede estar vacío.");
            return "redirect:/vivienda/" + viviendaId + "/nuevo-roommate";
        }

        if (!nombreReal.matches("^[a-zA-Z\\s]+$")) {
            redirectAttributes.addFlashAttribute("error", "Formato incorrecto: Solo letras y espacios.");
            return "redirect:/vivienda/" + viviendaId + "/nuevo-roommate";
        }

       
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(viviendaId);
        if (viviendaOpt.isEmpty()) {
            return "redirect:/listar"; 
        }
        Vivienda viviendaEncontrada = viviendaOpt.get();

     
        boolean existeDuplicado = roommateRepository.existsByNombreRealAndVivienda(nombreReal, viviendaEncontrada);
        if (existeDuplicado) {
            redirectAttributes.addFlashAttribute("error", "Ese compañero ya vive en esta casa.");
            return "redirect:/vivienda/" + viviendaId + "/nuevo-roommate";
        }     
        Roommate nuevoRoommate = new Roommate(nombreReal, viviendaEncontrada);
        roommateRepository.save(nuevoRoommate);

        return "redirect:/vivienda/" + viviendaId;
    }
   @GetMapping("/vivienda/{id}/roommates")
public String listarRoommates(@PathVariable("id") Long id, Model model) {
    Optional<Vivienda> viviendaOpt = viviendaRepository.findById(id);
    
    if (viviendaOpt.isPresent()) {
        Vivienda vivienda = viviendaOpt.get();
        model.addAttribute("vivienda", vivienda);
        model.addAttribute("viviendaId", id);
        model.addAttribute("viviendaNombre", vivienda.getName());
        return "listRoommates"; // Nombre del nuevo HTML
    }
    
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
           model.addAttribute("viviendaDescripcion", vivienda.getDescription());
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
       Vivienda vivienda = viviendaOpt.get();
       tarea.setVivienda(vivienda); 
       
       model.addAttribute("tarea", tarea);
       model.addAttribute("viviendaId", id); 
       model.addAttribute("viviendaNombre", vivienda.getName()); 
       
       return "crearTarea";
   }
   
   @PostMapping("/guardarTarea")
   public String guardarTarea(
            @RequestParam("name") String name,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("viviendaId") Long viviendaId,
            Model model) {
        
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(viviendaId);
        if (viviendaOpt.isEmpty()) {
            return "redirect:/listar";
        }
        Vivienda vivienda = viviendaOpt.get();
        String viviendaNombre = vivienda.getName(); 

        Tarea tareaTemporal = new Tarea();
        tareaTemporal.setName(name); 
        tareaTemporal.setDescripcion(descripcion);
        tareaTemporal.setVivienda(vivienda); 

    
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "No se han rellenado todos los campos obligatorios.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            model.addAttribute("viviendaNombre", viviendaNombre); 
            return "crearTarea";
        }

        String regexNombre = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]+$";
        String regexDescripcion = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ.,!?;:()]*$"; 

        if (!name.matches(regexNombre)) {
            model.addAttribute("error", "El formato del nombre no es válido.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            model.addAttribute("viviendaNombre", viviendaNombre); 
            return "crearTarea";
        }

        if (descripcion != null && !descripcion.isEmpty() && !descripcion.matches(regexDescripcion)) {
            model.addAttribute("error", "El formato de la descripción no es válido.");
            model.addAttribute("tarea", tareaTemporal); 
            model.addAttribute("viviendaId", viviendaId);
            model.addAttribute("viviendaNombre", viviendaNombre); 
            return "crearTarea";
        }

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
@PostMapping("/asignarTarea")
    public String processAsignTarea(
            @RequestParam(value = "tareaId", required = false) Long tareaId,
            @RequestParam(value = "roommateId", required = false) Long roommateId,
            Model model) {

        // CM5-2: Validamos si faltan datos
        if (tareaId == null || roommateId == null) {
            model.addAttribute("errorAsignacion", "Los datos son obligatorios.");
            model.addAttribute("tareas", tareaRepository.findAll());
            model.addAttribute("roommates", roommateRepository.findAll());
            return "asignarTarea"; 
        }

        // CM5-1: Si están los datos, asignamos
        Optional<Tarea> tareaOpt = tareaRepository.findById(tareaId);
        Optional<Roommate> roommateOpt = roommateRepository.findById(roommateId);

        if (tareaOpt.isPresent() && roommateOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            Roommate roommate = roommateOpt.get();
            
            // Si el método en tu entidad Tarea se llama diferente, cámbialo aquí
            tarea.setAsignadoA(roommate); 
            tareaRepository.save(tarea);
            
            return "redirect:/listar"; 
        }

        model.addAttribute("errorAsignacion", "La tarea o el roommate seleccionado no existe.");
        model.addAttribute("tareas", tareaRepository.findAll());
        model.addAttribute("roommates", roommateRepository.findAll());
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

    @PostMapping("/assignDate/submit")
    public String assignDate(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("taskId") Long taskId,
            @RequestParam("viviendaId") Long viviendaId,
            RedirectAttributes redirectAttributes) {

        Optional<Tarea> tareaOpt = tareaRepository.findById(taskId);

        if (tareaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error: La tarea seleccionada no existe.");
            return "redirect:/vivienda/" + viviendaId + "/listTareas";
        }
        Tarea tarea = tareaOpt.get();
        if (fecha == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Formato de fecha no válido.");
            return "redirect:/vivienda/" + viviendaId + "/listTareas";
        }

        if (!fecha.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMsg", "La fecha indicada no es válida. Debe ser posterior al día de hoy.");
            return "redirect:/vivienda/" + viviendaId + "/listTareas";
        }
        if (Boolean.TRUE.equals(tarea.getCompletada())){
            redirectAttributes.addFlashAttribute("errorMsg", "La tarea ya está completada.");
            return "redirect:/vivienda/" + viviendaId + "/listTareas";
        }

        tarea.setFechaRealizacion(fecha);
        tareaRepository.save(tarea);

        redirectAttributes.addFlashAttribute("successMsg", "Fecha asignada correctamente.");
        return "redirect:/vivienda/" + viviendaId + "/listTareas";
    }
    @PostMapping("/assignRoommate/submit")
    public String assignRoommate(
        @RequestParam(value = "roommateId", required = false) Long roommateId,
        @RequestParam("taskId") Long taskId,
        @RequestParam("viviendaId") Long viviendaId,
        RedirectAttributes redirectAttributes) {

    
    if (roommateId == null) {
        redirectAttributes.addFlashAttribute("errorMsg", "Error: Debe seleccionar un roommate para asignar la tarea.");
        return "redirect:/vivienda/" + viviendaId + "/listTareas";
    }

    Optional<Tarea> tareaOpt = tareaRepository.findById(taskId);
    Optional<Roommate> roommateOpt = roommateRepository.findById(roommateId);

    if (tareaOpt.isPresent() && roommateOpt.isPresent()) {
        Tarea tarea = tareaOpt.get();
        tarea.setAsignadoA(roommateOpt.get());
        tareaRepository.save(tarea);
        redirectAttributes.addFlashAttribute("successMsg", "Responsable asignado correctamente.");
    } else {
        redirectAttributes.addFlashAttribute("errorMsg", "Error al procesar la asignación.");
    }

    return "redirect:/vivienda/" + viviendaId + "/listTareas";
    }

   @GetMapping("/vivienda/{id}/calendario")
public String mostrarCalendario(@PathVariable("id") Long id, 
                                @RequestParam(value = "tareaId", required = false) Long tareaId, 
                                Model model) {
    
    Optional<Vivienda> viviendaOpt = viviendaRepository.findById(id);
    
    if (viviendaOpt.isPresent()) {
        model.addAttribute("vivienda", viviendaOpt.get());
        model.addAttribute("viviendaId", id);
        model.addAttribute("tareaId", tareaId);
        
        // 1. Tareas sin fecha (para el desplegable)
        List<Tarea> pendientes = tareaRepository.findByVivienda_IdAndFechaRealizacionIsNull(id);
        model.addAttribute("tareasPendientes", pendientes);
        
        // 2. Tareas CON fecha (para pintar el calendario)
        // Necesitas crear este método en el Repositorio si no existe
        List<Tarea> asignadas = tareaRepository.findByVivienda_IdAndFechaRealizacionIsNotNull(id);
        model.addAttribute("tareasAsignadas", asignadas);
        
        return "calendario"; 
    }
    return "redirect:/listar";
}

    @PostMapping("/api/asignar-tarea")
    @ResponseBody
    public org.springframework.http.ResponseEntity<String> asignarTareaAjax(
            @RequestParam("tareaId") Long tareaId, 
            @RequestParam("fecha") String fechaStr) {
        
        // CORREGIDO: tareaRepository con 't' minúscula
        Optional<Tarea> tareaOpt = tareaRepository.findById(tareaId);
        
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            // Asigna la fecha a tu campo real
            tarea.setFechaRealizacion(java.time.LocalDate.parse(fechaStr)); 
            tareaRepository.save(tarea);
            return org.springframework.http.ResponseEntity.ok("Asignado correctamente");
        }
        return org.springframework.http.ResponseEntity.badRequest().body("Error al asignar");
    }
   /* @GetMapping("/vivienda/foto/{id}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") Long id) {
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(id);
        
        if (viviendaOpt.isPresent() && viviendaOpt.get().getImage() != null) {
            // Extraemos los bytes (asegúrate de que tu método get de la entidad se llama getImage() o getImagen())
            byte[] imagenBytes = viviendaOpt.get().getImage(); 
            
            return org.springframework.http.ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "image/jpeg") // O image/png
                    .body(imagenBytes);
        }
        
        return org.springframework.http.ResponseEntity.notFound().build();
    }*/
}