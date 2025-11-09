package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/profesor")
public class ProfesorController {

    private final ProfessorService service;
    private final com.example.demo.repository.CursoRepository cursoRepository;

    @Autowired
    private SemanaService semanaService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private EntregaTareaService entregaTareaService;

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
                return "redirect:/error/acceso-denegado";
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
            cursoRepository.findById(id).ifPresent(curso -> {
                model.addAttribute("curso", curso);
                // Cargar semanas del curso
                List<Semana> semanas = semanaService.findByCursoId(id);
                model.addAttribute("semanas", semanas);
            });
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

    // ===========================
    // API PARA GESTIÓN DE SEMANAS
    // ===========================
    @PostMapping("/cursos/{cursoId}/semanas")
    @ResponseBody
    public ResponseEntity<?> createSemana(@PathVariable Long cursoId, @RequestBody Map<String, Object> payload, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            // Verificar que el curso pertenece al profesor
            Curso curso = cursoRepository.findById(cursoId).orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
            if (!curso.getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para gestionar este curso");
            }

            int numeroSemana = Integer.parseInt(payload.get("numeroSemana").toString());
            String titulo = (String) payload.get("titulo");
            String descripcion = (String) payload.get("descripcion");

            Semana semana = semanaService.createSemana(numeroSemana, titulo, descripcion, curso);
            return ResponseEntity.ok(semana);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creando semana: " + e.getMessage());
        }
    }

    @GetMapping("/cursos/{cursoId}/semanas")
    @ResponseBody
    public List<Semana> getSemanasByCurso(@PathVariable Long cursoId, Authentication authentication) {
        String email = authentication.getName();
        Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

        // Verificar que el curso pertenece al profesor
        Curso curso = cursoRepository.findById(cursoId).orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        if (!curso.getProfesor().getId().equals(profesor.getId())) {
            throw new IllegalStateException("No tienes permiso para ver este curso");
        }

        return semanaService.findByCursoId(cursoId);
    }

    // ===========================
    // API PARA GESTIÓN DE MATERIAL
    // ===========================
    @PostMapping("/semanas/{semanaId}/materiales")
    @ResponseBody
    public ResponseEntity<?> createMaterial(@PathVariable Long semanaId, @RequestBody Map<String, Object> payload, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            Semana semana = semanaService.findById(semanaId).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));

            // Verificar que la semana pertenece a un curso del profesor
            if (!semana.getCurso().getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para gestionar esta semana");
            }

            String nombre = (String) payload.get("nombre");
            String descripcion = (String) payload.get("descripcion");

            // Verificar si se envió un archivo
            String fileName = null;
            String fileType = null;
            byte[] fileData = null;

            if (payload.containsKey("fileName") && payload.containsKey("fileType") && payload.containsKey("fileData")) {
                fileName = (String) payload.get("fileName");
                fileType = (String) payload.get("fileType");
                String fileDataBase64 = (String) payload.get("fileData");

                // Convertir base64 a bytes
                fileData = java.util.Base64.getDecoder().decode(fileDataBase64);
            }

            Material material = materialService.createMaterial(nombre, fileName, fileType, fileData, descripcion, semana, profesor);
            return ResponseEntity.ok(material);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creando material: " + e.getMessage());
        }
    }

    @GetMapping("/semanas/{semanaId}/materiales")
    @ResponseBody
    public List<Material> getMaterialesBySemana(@PathVariable Long semanaId, Authentication authentication) {
        String email = authentication.getName();
        Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

        Semana semana = semanaService.findById(semanaId).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));
        if (!semana.getCurso().getProfesor().getId().equals(profesor.getId())) {
            throw new IllegalStateException("No tienes permiso para ver esta semana");
        }

        return materialService.findBySemanaId(semanaId);
    }

    @DeleteMapping("/materiales/{materialId}")
    @ResponseBody
    public ResponseEntity<?> deleteMaterial(@PathVariable Long materialId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            Material material = materialService.findById(materialId).orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));
            if (!material.getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para eliminar este material");
            }

            materialService.delete(materialId);
            return ResponseEntity.ok("Material eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando material: " + e.getMessage());
        }
    }

    // ===========================
    // API PARA GESTIÓN DE TAREAS
    // ===========================
    @PostMapping("/semanas/{semanaId}/tareas")
    @ResponseBody
    public ResponseEntity<?> createTarea(@PathVariable Long semanaId, @RequestBody Map<String, Object> payload, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            Semana semana = semanaService.findById(semanaId).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));
            if (!semana.getCurso().getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para gestionar esta semana");
            }

            String titulo = (String) payload.get("titulo");
            String descripcion = (String) payload.get("descripcion");
            LocalDateTime fechaLimite = LocalDateTime.parse((String) payload.get("fechaLimite"));
            int puntosMaximos = Integer.parseInt(payload.get("puntosMaximos").toString());

            Tarea tarea = tareaService.createTarea(titulo, descripcion, fechaLimite, puntosMaximos, semana, profesor);
            return ResponseEntity.ok(tarea);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creando tarea: " + e.getMessage());
        }
    }

    @GetMapping("/semanas/{semanaId}/tareas")
    @ResponseBody
    public List<Tarea> getTareasBySemana(@PathVariable Long semanaId, Authentication authentication) {
        String email = authentication.getName();
        Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

        Semana semana = semanaService.findById(semanaId).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));
        if (!semana.getCurso().getProfesor().getId().equals(profesor.getId())) {
            throw new IllegalStateException("No tienes permiso para ver esta semana");
        }

        return tareaService.findBySemanaId(semanaId);
    }

    @GetMapping("/tareas/{tareaId}/entregas")
    @ResponseBody
    public List<EntregaTarea> getEntregasByTarea(@PathVariable Long tareaId, Authentication authentication) {
        String email = authentication.getName();
        Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

        Tarea tarea = tareaService.findById(tareaId).orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        if (!tarea.getProfesor().getId().equals(profesor.getId())) {
            throw new IllegalStateException("No tienes permiso para ver esta tarea");
        }

        return entregaTareaService.findByTareaId(tareaId);
    }

    @PutMapping("/entregas/{entregaId}/calificar")
    @ResponseBody
    public ResponseEntity<?> calificarEntrega(@PathVariable Long entregaId, @RequestBody Map<String, Integer> payload, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            EntregaTarea entrega = entregaTareaService.findById(entregaId).orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada"));
            if (!entrega.getTarea().getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para calificar esta entrega");
            }

            int calificacion = payload.get("calificacion");
            EntregaTarea calificada = entregaTareaService.calificarEntrega(entregaId, calificacion);
            return ResponseEntity.ok(calificada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calificando entrega: " + e.getMessage());
        }
    }

    @DeleteMapping("/tareas/{tareaId}")
    @ResponseBody
    public ResponseEntity<?> deleteTarea(@PathVariable Long tareaId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            Tarea tarea = tareaService.findById(tareaId).orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
            if (!tarea.getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para eliminar esta tarea");
            }

            tareaService.delete(tareaId);
            return ResponseEntity.ok("Tarea eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando tarea: " + e.getMessage());
        }
    }

    @GetMapping("/materiales/{materialId}/download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable Long materialId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Professor profesor = service.findByEmail(email).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));

            Material material = materialService.findById(materialId).orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));
            if (!material.getProfesor().getId().equals(profesor.getId())) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(material.getFileType()))
                .body(material.getFileData());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
