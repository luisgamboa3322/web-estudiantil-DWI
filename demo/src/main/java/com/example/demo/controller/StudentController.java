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
import java.time.LocalDateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    private JwtUtil jwtUtil;

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

    // ===========================
    // API REST PARA ANGULAR
    // ===========================
    
    @GetMapping("/api/dashboard")
    @ResponseBody
    public Map<String, Object> getDashboardData(HttpServletRequest request) {
        System.out.println("🔍 DEBUG: Endpoint /student/api/dashboard llamado");
        
        validateJwtTokenFromRequest(request);
        
        String email = jwtUtil.getEmailFromToken(extractTokenFromRequest(request));
        Student student = studentService.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
        
        List<StudentCurso> cursosAsignados = studentCursoService.findByStudentId(student.getId());
        boolean mostrarMensajeNoCursos = cursosAsignados.isEmpty() || 
            cursosAsignados.stream().noneMatch(a -> a.getEstado() == EstadoAsignacion.ACTIVO);
        
        Map<String, Object> data = new HashMap<>();
        data.put("studentName", student.getNombre());
        data.put("student", student);
        data.put("cursosAsignados", cursosAsignados);
        data.put("mostrarMensajeNoCursos", mostrarMensajeNoCursos);
        data.put("activePage", "dashboard");
        
        System.out.println("✅ Student Dashboard Data - Student: " + student.getNombre() + 
                          ", Cursos: " + cursosAsignados.size());
        
        return data;
    }

    @GetMapping("/api/cursos-asignados")
    @ResponseBody
    public List<StudentCurso> getCursosAsignados(HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        
        String email = jwtUtil.getEmailFromToken(extractTokenFromRequest(request));
        Student student = studentService.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
        
        return studentCursoService.findByStudentId(student.getId());
    }

    @GetMapping("/api/curso/{cursoId}")
    @ResponseBody
    public Map<String, Object> getCursoDetalle(@PathVariable Long cursoId, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        
        String email = jwtUtil.getEmailFromToken(extractTokenFromRequest(request));
        Student student = studentService.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        // Verificar que el estudiante está asignado al curso
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(cursoId) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            throw new SecurityException("No tienes acceso a este curso");
        }

        // Obtener información del curso
        var asignacionOpt = studentCursoService.findByStudentId(student.getId()).stream()
            .filter(sc -> sc.getCurso().getId().equals(cursoId))
            .findFirst();

        if (!asignacionOpt.isPresent()) {
            throw new IllegalStateException("Curso no encontrado");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("curso", asignacionOpt.get().getCurso());
        data.put("asignacion", asignacionOpt.get());
        data.put("studentName", student.getNombre());
        data.put("activePage", "cursos");

        return data;
    }

    private void validateJwtTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Token JWT requerido");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Token JWT inválido");
        }
        
        Map<String, Object> authorities = jwtUtil.getAuthoritiesFromToken(token);
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) authorities.get("permissions");
        
        boolean hasStudentPermission = permissions != null &&
            permissions.contains("ACCESS_STUDENT_DASHBOARD");
        
        if (!hasStudentPermission) {
            throw new SecurityException("Acceso denegado - Permiso estudiante requerido");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new SecurityException("Token JWT no encontrado");
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

    // ===========================
    // VISTA DE DETALLE DE CURSO
    // ===========================
    @GetMapping("/curso/{cursoId}")
    public String showCursoDetalle(@PathVariable Long cursoId, Authentication authentication, Model model) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return "redirect:/error/acceso-denegado";
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        // Verificar que el estudiante está asignado al curso
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(cursoId) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return "redirect:/error/acceso-denegado";
        }

        // Obtener información del curso
        var asignacionOpt = studentCursoService.findByStudentId(student.getId()).stream()
            .filter(sc -> sc.getCurso().getId().equals(cursoId))
            .findFirst();

        if (asignacionOpt.isPresent()) {
            model.addAttribute("curso", asignacionOpt.get().getCurso());
            model.addAttribute("asignacion", asignacionOpt.get());
            model.addAttribute("studentName", student.getNombre());
            model.addAttribute("activePage", "cursos");
        }

        return "student/curso-detalle";
    }

    // ===========================
    // APIs PARA CONTENIDO DINÁMICO
    // ===========================

    @GetMapping("/cursos/{cursoId}/semanas")
    @ResponseBody
    public List<Semana> getSemanasByCurso(@PathVariable Long cursoId, Authentication authentication) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return new ArrayList<>();
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null) return new ArrayList<>();

        // Verificar que el estudiante está asignado al curso
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(cursoId) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return new ArrayList<>();
        }

        return semanaService.findByCursoId(cursoId);
    }

    @GetMapping("/semanas/{semanaId}/materiales")
    @ResponseBody
    public List<Material> getMaterialesBySemana(@PathVariable Long semanaId, Authentication authentication) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return new ArrayList<>();
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null) return new ArrayList<>();

        // Verificar que la semana pertenece a un curso asignado al estudiante
        Semana semana = semanaService.findById(semanaId).orElse(null);
        if (semana == null) return new ArrayList<>();

        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(semana.getCurso().getId())
                      && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return new ArrayList<>();
        }

        return materialService.findBySemanaId(semanaId);
    }

    @GetMapping("/semanas/{semanaId}/tareas")
    @ResponseBody
    public List<Tarea> getTareasBySemana(@PathVariable Long semanaId, Authentication authentication) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return new ArrayList<>();
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null) return new ArrayList<>();

        // Verificar que la semana pertenece a un curso asignado al estudiante
        Semana semana = semanaService.findById(semanaId).orElse(null);
        if (semana == null) return new ArrayList<>();

        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(semana.getCurso().getId())
                      && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return new ArrayList<>();
        }

        return tareaService.findBySemanaId(semanaId);
    }

    @GetMapping("/tareas/{tareaId}/entrega")
    @ResponseBody
    public Map<String, Object> getEstadoEntrega(@PathVariable Long tareaId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("entregada", false);

        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return response;
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null) return response;

        Optional<EntregaTarea> entregaOpt = entregaTareaService.findByTareaIdAndStudentId(tareaId, student.getId());

        if (entregaOpt.isPresent()) {
            EntregaTarea entrega = entregaOpt.get();
            response.put("entregada", true);
            response.put("calificada", entrega.isCalificada());
            if (entrega.isCalificada()) {
                response.put("calificacion", entrega.getCalificacion());
            }
        }

        return response;
    }

    @PostMapping("/tareas/{tareaId}/entregar")
    @ResponseBody
    public ResponseEntity<?> entregarTarea(@PathVariable Long tareaId,
                                         @RequestBody Map<String, String> payload,
                                         Authentication authentication) {
        try {
            // Verificar permisos
            boolean hasStudentPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
            if (!hasStudentPermission) {
                return ResponseEntity.status(403).body("Acceso denegado");
            }

            String email = authentication.getName();
            Student student = studentService.findByEmail(email).orElse(null);

            if (student == null) {
                return ResponseEntity.status(404).body("Estudiante no encontrado");
            }

            String contenido = payload.get("contenido");
            if (contenido == null || contenido.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El contenido no puede estar vacío");
            }

            // Verificar que la tarea existe y pertenece a un curso asignado al estudiante
            Tarea tarea = tareaService.findById(tareaId).orElse(null);
            if (tarea == null) {
                return ResponseEntity.status(404).body("Tarea no encontrada");
            }

            // Verificar que el estudiante está asignado al curso de la tarea
            boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(tarea.getSemana().getCurso().getId())
                          && sc.getEstado() == EstadoAsignacion.ACTIVO);

            if (!estaAsignado) {
                return ResponseEntity.status(403).body("No tienes acceso a esta tarea");
            }

            // Verificar que no haya entregado ya
            Optional<EntregaTarea> entregaExistente = entregaTareaService.findByTareaIdAndStudentId(tareaId, student.getId());
            if (entregaExistente.isPresent()) {
                return ResponseEntity.badRequest().body("Ya has entregado esta tarea");
            }

            // Crear la entrega
            EntregaTarea entrega = new EntregaTarea();
            entrega.setTarea(tarea);
            entrega.setStudent(student);
            entrega.setContenido(contenido.trim());
            entrega.setFechaEntrega(LocalDateTime.now());

            entregaTareaService.save(entrega);

            return ResponseEntity.ok().body("Tarea entregada exitosamente");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    @ResponseBody
    public Student updateProfile(Authentication authentication, @RequestBody Student changes) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
        return studentService.update(student.getId(), changes);
    }

    @GetMapping("/materiales/{materialId}/download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable Long materialId, Authentication authentication) {
        try {
            // Verificar permisos
            boolean hasStudentPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
            if (!hasStudentPermission) {
                return ResponseEntity.status(403).build();
            }

            String email = authentication.getName();
            Student student = studentService.findByEmail(email).orElse(null);

            if (student == null) {
                return ResponseEntity.status(404).build();
            }

            Material material = materialService.findById(materialId).orElse(null);
            if (material == null) {
                return ResponseEntity.status(404).build();
            }

            // Verificar que el material pertenece a una semana de un curso asignado al estudiante
            boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(material.getSemana().getCurso().getId())
                          && sc.getEstado() == EstadoAsignacion.ACTIVO);

            if (!estaAsignado) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(material.getFileType()))
                .body(material.getFileData());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}