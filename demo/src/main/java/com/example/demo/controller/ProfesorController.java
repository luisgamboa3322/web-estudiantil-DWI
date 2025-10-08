package com.example.demo.controller;

import com.example.demo.model.Professor;
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

    public ProfesorController(ProfessorService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        if (email != null) {
            Optional<Professor> opt = service.findByEmail(email);
            if (opt.isPresent()) {
                model.addAttribute("profesor", opt.get());
                return "profesor/dashboard";
            }
        }
        // fallback: redirigir al login si no se encuentra el profesor
        return "redirect:/login?error=no_profesor";
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
