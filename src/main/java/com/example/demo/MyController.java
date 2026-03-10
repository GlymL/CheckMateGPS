package com.example.demo;

import com.example.demo.model.Vivienda;
import com.example.demo.repository.ViviendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador MVC encargado de manejar las peticiones web tradicionales 
 * y devolver las plantillas HTML (vistas) correspondientes.
 */
@Controller
public class MyController {

    private final ViviendaRepository viviendaRepository;

    /**
     * Constructor del controlador que inyecta las dependencias necesarias.
     * * @param viviendaRepository El repositorio que proporciona las operaciones de base de datos para la entidad Vivienda.
     */
    @Autowired
    public MyController(ViviendaRepository viviendaRepository) {
        this.viviendaRepository = viviendaRepository;
    }

    /**
     * Maneja la petición GET a la ruta raíz y muestra la página de inicio.
     *
     * @param modelo El objeto Model proporcionado por Spring, utilizado para pasar datos desde el backend a la vista HTML.
     * @return Un String que representa el nombre de la plantilla HTML a renderizar ("index").
     */
    @GetMapping("/")
    public String home(Model modelo) {
        return "index";
    }

    /**
     * Maneja la petición POST a la ruta "/submit", ejecuta una acción en el servidor
     * instanciando la clase Dot, y redirige al usuario a la página de resultados.
     *
     * @param modelo El objeto Model proporcionado por Spring para enviar mensajes de estado a la vista de resultados.
     * @return Un String que representa el nombre de la plantilla HTML de destino ("result").
     */
    @PostMapping("/submit")
    public String submit(Model modelo) {
        System.out.println("Button clicked!");
        
        // Ejecuta la lógica auxiliar
        new Dot(); 
        
        // Pasamos un mensaje a la vista para confirmar que funcionó
        modelo.addAttribute("mensaje", "La acción de prueba se completó correctamente.");
        
        return "result";
    }

    /**
     * Intercepta la petición POST para crear una nueva vivienda, instanciando 
     * el modelo, guardándolo en la base de datos y redirigiendo a la vista de resultados.
     *
     * @param direccion El texto ingresado por el usuario correspondiente a la dirección de la vivienda.
     * @param modelo El objeto Model proporcionado por Spring para enviar mensajes de éxito a la vista.
     * @return Un String que representa el nombre de la plantilla HTML de destino ("result").
     */
    @PostMapping("/crear-vivienda")
    public String crearVivienda(@RequestParam("direccion") String direccion, Model modelo) {
        
        // 1. Instanciamos la nueva entidad Vivienda
        Vivienda nuevaVivienda = new Vivienda();
        nuevaVivienda.setDireccion(direccion);
        
        // 2. Guardamos la entidad en la base de datos usando el Repositorio
        viviendaRepository.save(nuevaVivienda);
        
        // 3. Enviamos un mensaje de confirmación a la vista HTML
        modelo.addAttribute("mensaje", "¡La vivienda en '" + direccion + "' ha sido guardada en la base de datos!");
        
        return "result";
    }
}