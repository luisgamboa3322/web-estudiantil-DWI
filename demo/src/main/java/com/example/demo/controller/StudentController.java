package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

import com.example.demo.service.StudentService;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        String nombre = (email != null)
            ? studentService.findByEmail(email).map(s -> s.getNombre()).orElse(email)
            : "Estudiante";
        model.addAttribute("studentName", nombre);
        model.addAttribute("activePage", "dashboard");
        // Tu lógica actual para el dashboard
        return "student/dashboard";
    }

    @GetMapping("/chat")
    public String showChat(Model model) {
        model.addAttribute("activePage", "chat");
        // Lógica para la vista de chat
        return "student/chat"; // Asegúrate de tener una vista chat.html
    }

    @GetMapping("/calendario")
    public String showCalendario(Model model) {
        model.addAttribute("activePage", "calendario");
        // Lógica para la vista de calendario
        return "student/calendario"; // Asegúrate de tener una vista calendario.html
    }

    @GetMapping("/configuracion")
    public String showConfiguracion(Model model) {
        model.addAttribute("activePage", "configuracion");
        // Lógica para la vista de configuración
        return "student/configuracion"; // Asegúrate de tener una vista configuracion.html
    }

    // Agrega métodos para las otras vistas (ayuda, etc.) de la misma manera
}