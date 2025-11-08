package com.example.demo.controller;

import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import com.example.demo.service.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profesor")
public class ProfesorController {

    private final ProfessorService service;
    private final com.example.demo.repository.CursoRepository cursoRepository;

    public ProfesorController(ProfessorService service, com.example.demo.repository.CursoRepository cursoRepository) {
        this.service = service;
        this.cursoRepository = cursoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        if (email != null) {
            // Verificar si el usuario tiene permiso para acceder al dashboard docente
            boolean hasTeacherPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_TEACHER_DASHBOARD"));

            if (!hasTeacherPermission) {
                // Usuario no tiene permiso para acceder al dashboard docente
                model.addAttribute("error", "Acceso denegado: No tienes permisos para acceder al dashboard docente");
                return "error/acceso-denegado";
            }

            Optional<Professor> opt = service.findByEmail(email);
            if (opt.isPresent()) {
                Professor profesor = opt.get();
                model.addAttribute("profesor", profesor);
                // cargar cursos asignados al profesor y exponerlos al modelo
                java.util.List<Curso> cursos = cursoRepository.findByProfesorId(profesor.getId());
                model.addAttribute("cursos", cursos);
                return "profesor/dashboard";
            } else {
                // Es un admin o usuario con permisos de docente pero no es profesor registrado
                // Crear un objeto profesor virtual para mostrar información básica
                Professor profesorVirtual = new Professor();
                profesorVirtual.setNombre("Usuario con permisos docentes");
                profesorVirtual.setEmail(email);
                profesorVirtual.setEspecialidad("Acceso administrativo");
                model.addAttribute("profesor", profesorVirtual);
                model.addAttribute("cursos", java.util.Collections.emptyList()); // Lista vacía de cursos
                return "profesor/dashboard";
            }
        }
        // fallback: redirigir al login si no hay autenticación
        return "redirect:/login?error=no_auth";
    }

    @GetMapping("/configuracion")
    public String showConfiguracion(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            service.findByEmail(email).ifPresent(professor -> {
                model.addAttribute("profesor", professor);
            });
        }
        return "profesor/configuracion";
    }

    @GetMapping("/cursos")
    public String showCursos(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            service.findByEmail(email).ifPresent(professor -> {
                model.addAttribute("profesor", professor);
                java.util.List<Curso> cursos = cursoRepository.findByProfesorId(professor.getId());
                model.addAttribute("cursos", cursos);
            });
        }
        return "profesor/dashboard"; // la vista de cursos está en dashboard
    }

    @GetMapping("/chat")
    public String showChat(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            service.findByEmail(email).ifPresent(professor -> model.addAttribute("profesor", professor));
        }
        return "profesor/chat";
    }

    @GetMapping("/calendario")
    public String showCalendario(Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            service.findByEmail(email).ifPresent(professor -> model.addAttribute("profesor", professor));
        }
        return "profesor/calendario";
    }

    @GetMapping("/gestion-curso")
    public String gestionCurso(@RequestParam(name = "id", required = false) Long id, Authentication authentication, Model model) {
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            service.findByEmail(email).ifPresent(professor -> model.addAttribute("profesor", professor));
        }
        if (id != null) {
            cursoRepository.findById(id).ifPresent(curso -> model.addAttribute("curso", curso));
        }
        return "profesor/gestion-curso";
    }

    @PutMapping("/profile")
    @ResponseBody
    public Professor updateProfile(Authentication authentication, @RequestBody Professor changes) {
        String email = authentication.getName();
        Professor professor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));
        return service.update(professor.getId(), changes);
    }

    @GetMapping
    public List<Professor> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> one(@PathVariable Long id) {
        return service.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Professor> create(@RequestBody Professor p) {
        Professor created = service.create(p);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> update(@PathVariable Long id, @RequestBody Professor p) {
        Professor updated = service.update(id, p);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
