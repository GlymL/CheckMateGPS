package com.example.demo;

import org.springframework.stereotype.Controller;
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


        try{
            ListaViviendas.getInstance().InsertVivienda(new Vivienda(houseName, description, image));
            return "redirect:/result";
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El nombre de una casa no puede existir ya, por favor, introduzca uno nuevo.");

            return "redirect:/";
            
        }
    }

    @GetMapping("/result")
    public String resultPage() {
        return "result";
    }
}