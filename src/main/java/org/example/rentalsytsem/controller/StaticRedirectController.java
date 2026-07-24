package org.example.rentalsytsem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticRedirectController {

    // Redirect root path and home.html to index.html
    @GetMapping({"/", "/home.html", "/home"})
    public String homeRedirect() {
        return "redirect:/index.html";
    }

    // Redirect plural or legacy paths to the actual static page
    @GetMapping({"/vehicles.html", "/vehicles", "/vehicles/"})
    public String vehiclesRedirect() {
        return "redirect:/vehicle.html";
    }
}
