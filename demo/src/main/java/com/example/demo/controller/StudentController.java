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
import com.example.demo.model.StudentCurso;
import com.example.demo.model.EstadoAsignacion;
import java.util.List;
import java.util.ArrayList;
import com.example.demo.service.StudentService;
import com.example.demo.service.StudentCursoService;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final StudentCursoService studentCursoService;

    public StudentController(StudentService studentService, StudentCursoService studentCursoService) {
        this.studentService = studentService;
        this.studentCursoService = studentCursoService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        System.out.println("DEBUG: Student dashboard accessed by: " + email);
        String nombre = (email != null)
            ? studentService.findByEmail(email).map(s -> s.getNombre()).orElse(email)
            : "Estudiante";
        model.addAttribute("studentName", nombre);
        model.addAttribute("activePage", "dashboard");

        // Obtener cursos asignados al estudiante
        List<StudentCurso> cursosAsignados = new ArrayList<>();
        if (email != null) {
            var studentOpt = studentService.findByEmail(email);
            if (studentOpt.isPresent()) {
                var student = studentOpt.get();
                cursosAsignados = studentCursoService.findByStudentId(student.getId());
                System.out.println("DEBUG: Student " + student.getNombre() + " has " + cursosAsignados.size() + " courses assigned");
            } else {
                System.out.println("DEBUG: Student not found for email: " + email);
            }
        } else {
            System.out.println("DEBUG: No authentication found");
        }
        model.addAttribute("cursosAsignados", cursosAsignados);

        boolean mostrarMensajeNoCursos = cursosAsignados.isEmpty() || cursosAsignados.stream().noneMatch(a -> a.getEstado() == EstadoAsignacion.ACTIVO);
        model.addAttribute("mostrarMensajeNoCursos", mostrarMensajeNoCursos);

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