package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.example.demo.model.*;
import com.example.demo.service.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final StudentCursoService studentCursoService;

    @Autowired
    private SemanaService semanaService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private EntregaTareaService entregaTareaService;

    public StudentController(StudentService studentService, StudentCursoService studentCursoService) {
        this.studentService = studentService;
        this.studentCursoService = studentCursoService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        // Verificar si el usuario tiene permiso para acceder al dashboard estudiante
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));

        if (!hasStudentPermission) {
            // Usuario no tiene permiso para acceder al dashboard estudiante
            return "redirect:/error/acceso-denegado";
        }

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

    // ===========================
    // API PARA VER CONTENIDO DE CURSOS
    // ===========================
    @GetMapping("/cursos/{cursoId}/semanas")
    @ResponseBody
    public List<Semana> getSemanasByCurso(@PathVariable Long cursoId, Authentication authentication) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        // Verificar que el estudiante está asignado al curso
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(cursoId) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            throw new IllegalStateException("No tienes acceso a este curso");
        }

        return semanaService.findByCursoId(cursoId);
    }

    @GetMapping("/semanas/{semanaId}/materiales")
    @ResponseBody
    public List<Material> getMaterialesBySemana(@PathVariable Long semanaId, Authentication authentication) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        Semana semana = semanaService.findById(semanaId).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));

        // Verificar que el estudiante está asignado al curso de la semana
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(semana.getCurso().getId()) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            throw new IllegalStateException("No tienes acceso a esta semana");
        }

        return materialService.findBySemanaId(semanaId);
    }

    @GetMapping("/semanas/{semanaId}/tareas")
    @ResponseBody
    public List<Tarea> getTareasBySemana(@PathVariable Long semanaId, Authentication authentication) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        Semana semana = semanaService.findById(semanaId).orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));

        // Verificar que el estudiante está asignado al curso de la semana
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(semana.getCurso().getId()) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            throw new IllegalStateException("No tienes acceso a esta semana");
        }

        return tareaService.findBySemanaId(semanaId);
    }

    // ===========================
    // API PARA ENTREGAR TAREAS
    // ===========================
    @PostMapping("/tareas/{tareaId}/entregar")
    @ResponseBody
    public ResponseEntity<?> entregarTarea(@PathVariable Long tareaId, @RequestBody Map<String, String> payload, Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

            Tarea tarea = tareaService.findById(tareaId).orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

            // Verificar que el estudiante está asignado al curso de la tarea
            boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(tarea.getSemana().getCurso().getId()) && sc.getEstado() == EstadoAsignacion.ACTIVO);

            if (!estaAsignado) {
                return ResponseEntity.status(403).body("No tienes acceso a esta tarea");
            }

            String contenido = payload.get("contenido");
            EntregaTarea entrega = entregaTareaService.entregarTarea(contenido, tarea, student);
            return ResponseEntity.ok(entrega);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error entregando tarea: " + e.getMessage());
        }
    }

    @GetMapping("/tareas/{tareaId}/entrega")
    @ResponseBody
    public ResponseEntity<?> getEstadoEntrega(@PathVariable Long tareaId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

            Tarea tarea = tareaService.findById(tareaId).orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

            // Verificar que el estudiante está asignado al curso de la tarea
            boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(tarea.getSemana().getCurso().getId()) && sc.getEstado() == EstadoAsignacion.ACTIVO);

            if (!estaAsignado) {
                return ResponseEntity.status(403).body("No tienes acceso a esta tarea");
            }

            Optional<EntregaTarea> entregaOpt = entregaTareaService.findByTareaIdAndStudentId(tareaId, student.getId());

            Map<String, Object> response = new HashMap<>();
            if (entregaOpt.isPresent()) {
                EntregaTarea entrega = entregaOpt.get();
                response.put("entregada", true);
                response.put("fechaEntrega", entrega.getFechaEntrega());
                response.put("calificada", entrega.isCalificada());
                response.put("calificacion", entrega.getCalificacion());
                response.put("contenido", entrega.getContenido());
            } else {
                response.put("entregada", false);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error obteniendo estado de entrega: " + e.getMessage());
        }
    }
}