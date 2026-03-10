package com.example.demo;

import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class MyController {

    private HashSet<String> nombreCasas = new HashSet<String>();

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


        if (nombreCasas.contains(houseName)) {

            redirectAttributes.addFlashAttribute("errorMessage",
                    "El nombre de una casa no puede existir ya, por favor, introduzca uno nuevo.");

            return "redirect:/";
        }
        nombreCasas.add(houseName);
        return "redirect:/result";
    }

    @GetMapping("/result")
    public String resultPage() {
        return "result";
    }
}