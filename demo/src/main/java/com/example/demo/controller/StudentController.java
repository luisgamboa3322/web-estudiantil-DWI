package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

import com.example.demo.model.Student;
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
    public String showConfiguracion(Authentication authentication, Model model) {
        model.addAttribute("activePage", "configuracion");
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            studentService.findByEmail(email).ifPresent(student -> {
                model.addAttribute("student", student);
            });
        }
        return "student/configuracion";
    }

    @PutMapping("/profile")
    @ResponseBody
    public Student updateProfile(Authentication authentication, @RequestBody Student changes) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
        return studentService.update(student.getId(), changes);
    }

    // Agrega métodos para las otras vistas (ayuda, etc.) de la misma manera
}