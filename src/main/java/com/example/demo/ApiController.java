package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de manejar las peticiones de la API de la aplicación,
 * devolviendo datos puros (texto, JSON) en lugar de vistas HTML.
 */
@RestController
public class ApiController {

    /**
     * Maneja la petición GET a la ruta "/api/button" y devuelve un mensaje de confirmación.
     *
     * @param origen Un parámetro opcional en la URL que indica desde dónde se hizo el clic (por defecto es "Usuario").
     * @return Un String confirmando que el botón fue pulsado en el servidor, incluyendo el origen.
     */
    @GetMapping("/api/button")
    public String buttonAction(@RequestParam(defaultValue = "Usuario") String origen) {
        return "Button clicked on server by: " + origen + "!";
    }
}