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
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.demo.dto.IntentoEvaluacionResumenDTO;

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
    private EvaluacionService evaluacionService;

    @Autowired
    private CalendarioService calendarioService;

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
                System.out.println("DEBUG: Student " + student.getNombre() + " has " + cursosAsignados.size()
                        + " courses assigned");
            } else {
                System.out.println("DEBUG: Student not found for email: " + email);
            }
        } else {
            System.out.println("DEBUG: No authentication found");
        }
        model.addAttribute("cursosAsignados", cursosAsignados);

        boolean mostrarMensajeNoCursos = cursosAsignados.isEmpty()
                || cursosAsignados.stream().noneMatch(a -> a.getEstado() == EstadoAsignacion.ACTIVO);
        model.addAttribute("mostrarMensajeNoCursos", mostrarMensajeNoCursos);

        return "student/dashboard";
    }

    @GetMapping("/chat")
    public String showChat(Authentication authentication, Model model) {
        model.addAttribute("activePage", "chat");

        // Obtener información del estudiante para el chat
        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            studentService.findByEmail(email).ifPresent(student -> {
                model.addAttribute("studentId", student.getId());
                model.addAttribute("studentNombre", student.getNombre());
            });
        }

        return "student/chat";
    }

    @GetMapping("/calendario")
    public String showCalendario(Authentication authentication, Model model) {
        model.addAttribute("activePage", "calendario");

        String email = authentication != null ? authentication.getName() : null;
        if (email != null) {
            studentService.findByEmail(email).ifPresent(student -> {
                model.addAttribute("studentName", student.getNombre());

                // Obtener cursos para el filtro
                List<StudentCurso> cursos = studentCursoService.findByStudentId(student.getId());
                model.addAttribute("cursos", cursos);
            });
        }

        return "student/calendario";
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
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

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

        if (student == null)
            return new ArrayList<>();

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

        if (student == null)
            return new ArrayList<>();

        // Verificar que la semana pertenece a un curso asignado al estudiante
        Semana semana = semanaService.findById(semanaId).orElse(null);
        if (semana == null)
            return new ArrayList<>();

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

        if (student == null)
            return new ArrayList<>();

        // Verificar que la semana pertenece a un curso asignado al estudiante
        Semana semana = semanaService.findById(semanaId).orElse(null);
        if (semana == null)
            return new ArrayList<>();

        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(semana.getCurso().getId())
                        && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return new ArrayList<>();
        }

        return tareaService.findBySemanaId(semanaId);
    }

    @GetMapping("/semanas/{semanaId}/evaluaciones")
    @ResponseBody
    public List<Evaluacion> getEvaluacionesBySemana(@PathVariable Long semanaId, Authentication authentication) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return new ArrayList<>();
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null)
            return new ArrayList<>();

        // Verificar que la semana pertenece a un curso asignado al estudiante
        Semana semana = semanaService.findById(semanaId).orElse(null);
        if (semana == null)
            return new ArrayList<>();

        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(semana.getCurso().getId())
                        && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return new ArrayList<>();
        }

        return evaluacionService.findBySemanaId(semanaId);
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

        if (student == null)
            return response;

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
            Optional<EntregaTarea> entregaExistente = entregaTareaService.findByTareaIdAndStudentId(tareaId,
                    student.getId());
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
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
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

            // Verificar que el material pertenece a una semana de un curso asignado al
            // estudiante
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

    @GetMapping("/evaluacion/resultados/{evaluacionId}")
    @Transactional(readOnly = true)
    public String verResultados(@PathVariable Long evaluacionId, Authentication authentication, Model model) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return "redirect:/error/acceso-denegado";
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        Evaluacion evaluacion = evaluacionService.findById(evaluacionId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        // Inicializar relaciones lazy necesarias para evitar errores de serialización
        Hibernate.initialize(evaluacion.getSemana());
        if (evaluacion.getSemana() != null) {
            Hibernate.initialize(evaluacion.getSemana().getCurso());
        }
        Hibernate.initialize(evaluacion.getProfesor());

        // Verificar que el estudiante está asignado al curso
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(evaluacion.getSemana().getCurso().getId())
                        && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return "redirect:/error/acceso-denegado";
        }

        // Obtener intentos del estudiante para esta evaluación
        List<IntentoEvaluacion> intentosEntidad = evaluacionService
                .getIntentosByEvaluacionIdAndEstudianteId(evaluacionId, student.getId());

        // Convertir a DTOs
        List<IntentoEvaluacionResumenDTO> intentos = new ArrayList<>();
        for (IntentoEvaluacion intento : intentosEntidad) {
            Hibernate.initialize(intento.getEstudiante());
            Hibernate.initialize(intento.getCalificacion());

            Calificacion calificacion = intento.getCalificacion();
            Student estudiante = intento.getEstudiante();

            IntentoEvaluacionResumenDTO dto = new IntentoEvaluacionResumenDTO(
                    intento.getId(),
                    intento.getEvaluacion() != null ? intento.getEvaluacion().getId() : null,
                    estudiante != null ? estudiante.getId() : null,
                    estudiante != null ? estudiante.getNombre() : null,
                    estudiante != null ? estudiante.getEmail() : null,
                    intento.getNumeroIntento(),
                    intento.getEstado(),
                    intento.getFechaInicio(),
                    intento.getFechaFin(),
                    calificacion != null ? calificacion.getCalificacion() : null,
                    calificacion != null ? calificacion.getCalificacionAutomatica() : null,
                    calificacion != null ? calificacion.getComentarios() : null);
            intentos.add(dto);
        }

        // Serializar a JSON para evitar problemas con la serialización de Thymeleaf
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String intentosJson = objectMapper.writeValueAsString(intentos);
            model.addAttribute("intentosJson", intentosJson);
        } catch (Exception e) {
            System.err.println("Error serializando intentos a JSON: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("intentosJson", "[]");
        }

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("intentos", intentos);
        model.addAttribute("studentName", student.getNombre());

        return "student/resultados-evaluacion";
    }

    @GetMapping("/evaluaciones/{evaluacionId}/intentos")
    @ResponseBody
    public List<IntentoEvaluacion> getIntentosEvaluacion(@PathVariable Long evaluacionId,
            Authentication authentication) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return new ArrayList<>();
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null)
            return new ArrayList<>();

        // Verificar que el estudiante está asignado al curso de la evaluación
        Evaluacion evaluacion = evaluacionService.findById(evaluacionId).orElse(null);
        if (evaluacion == null)
            return new ArrayList<>();

        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(evaluacion.getSemana().getCurso().getId())
                        && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return new ArrayList<>();
        }

        return evaluacionService.getIntentosByEvaluacionIdAndEstudianteId(evaluacionId, student.getId());
    }

    @PostMapping("/evaluaciones/{evaluacionId}/iniciar")
    @ResponseBody
    public ResponseEntity<?> iniciarEvaluacion(@PathVariable Long evaluacionId, Authentication authentication) {
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

            // Verificar que el estudiante está asignado al curso de la evaluación
            Evaluacion evaluacion = evaluacionService.findById(evaluacionId)
                    .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

            boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                    .anyMatch(sc -> sc.getCurso().getId().equals(evaluacion.getSemana().getCurso().getId())
                            && sc.getEstado() == EstadoAsignacion.ACTIVO);

            if (!estaAsignado) {
                return ResponseEntity.status(403).body("No tienes acceso a esta evaluación");
            }

            IntentoEvaluacion intento = evaluacionService.iniciarIntento(evaluacionId, student.getId());
            return ResponseEntity.ok(intento);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/evaluacion/{intentoId}")
    @Transactional(readOnly = true)
    public String realizarEvaluacion(@PathVariable Long intentoId, Authentication authentication, Model model) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return "redirect:/error/acceso-denegado";
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        IntentoEvaluacion intento = evaluacionService.findIntentoById(intentoId)
                .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));

        // Verificar que el intento pertenece al estudiante
        if (!intento.getEstudiante().getId().equals(student.getId())) {
            return "redirect:/error/acceso-denegado";
        }

        // Verificar que el intento está en progreso
        if (intento.getEstado() != EstadoIntento.EN_PROGRESO) {
            return "redirect:/student/evaluacion/resultados/" + intento.getEvaluacion().getId();
        }

        Evaluacion evaluacion = intento.getEvaluacion();

        // Inicializar relaciones lazy
        Hibernate.initialize(evaluacion.getPreguntas());
        for (Pregunta pregunta : evaluacion.getPreguntas()) {
            Hibernate.initialize(pregunta.getOpciones());
        }
        Hibernate.initialize(intento.getRespuestas());

        // Cargar respuestas guardadas
        List<RespuestaEstudiante> respuestasGuardadas = intento.getRespuestas();

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("intento", intento);
        model.addAttribute("preguntas", evaluacion.getPreguntas());
        model.addAttribute("respuestasGuardadas", respuestasGuardadas);
        model.addAttribute("studentName", student.getNombre());

        return "student/realizar-evaluacion";
    }

    @PostMapping("/intentos/{intentoId}/respuestas")
    @ResponseBody
    public ResponseEntity<?> guardarRespuesta(@PathVariable Long intentoId,
            @RequestBody Map<String, Object> payload,
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

            // Verificar que el intento pertenece al estudiante
            IntentoEvaluacion intento = evaluacionService.findIntentoById(intentoId)
                    .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));

            if (!intento.getEstudiante().getId().equals(student.getId())) {
                return ResponseEntity.status(403).body("No tienes acceso a este intento");
            }

            Long preguntaId = Long.parseLong(payload.get("preguntaId").toString());
            String respuestaTexto = payload.containsKey("respuestaTexto") ? (String) payload.get("respuestaTexto")
                    : null;
            Long opcionSeleccionadaId = payload.containsKey("opcionSeleccionadaId")
                    && payload.get("opcionSeleccionadaId") != null
                            ? Long.parseLong(payload.get("opcionSeleccionadaId").toString())
                            : null;
            String opcionesOrdenadas = payload.containsKey("opcionesOrdenadas")
                    ? (String) payload.get("opcionesOrdenadas")
                    : null;

            RespuestaEstudiante respuesta = evaluacionService.guardarRespuesta(
                    intentoId, preguntaId, respuestaTexto, opcionSeleccionadaId, opcionesOrdenadas);

            return ResponseEntity.ok(respuesta);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/intentos/{intentoId}/finalizar")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> finalizarIntento(@PathVariable Long intentoId, Authentication authentication) {
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

            // Verificar que el intento pertenece al estudiante
            IntentoEvaluacion intento = evaluacionService.findIntentoById(intentoId)
                    .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));

            if (!intento.getEstudiante().getId().equals(student.getId())) {
                return ResponseEntity.status(403).body("No tienes acceso a este intento");
            }

            IntentoEvaluacion intentoFinalizado = evaluacionService.finalizarIntento(intentoId);

            // Inicializar relaciones necesarias para evitar problemas de serialización
            Hibernate.initialize(intentoFinalizado.getEvaluacion());
            if (intentoFinalizado.getEvaluacion() != null) {
                Hibernate.initialize(intentoFinalizado.getEvaluacion().getId());
            }

            // Devolver solo la información necesaria en lugar del objeto completo
            Map<String, Object> response = new HashMap<>();
            response.put("id", intentoFinalizado.getId());
            response.put("estado", intentoFinalizado.getEstado());
            response.put("evaluacionId",
                    intentoFinalizado.getEvaluacion() != null ? intentoFinalizado.getEvaluacion().getId() : null);
            response.put("fechaFin", intentoFinalizado.getFechaFin());

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    // ===========================
    // ENDPOINTS DEL CALENDARIO
    // ===========================

    /**
     * Obtener todos los eventos del calendario del estudiante
     * Incluye: eventos personales, tareas y evaluaciones
     */
    @GetMapping("/calendario/eventos")
    @ResponseBody
    public ResponseEntity<?> getEventosCalendario(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

            LocalDateTime inicio;
            if (fechaInicio != null) {
                // Manejar formato ISO 8601 (ej: 2025-10-25T05:00:00.000Z)
                inicio = java.time.ZonedDateTime.parse(fechaInicio).toLocalDateTime();
            } else {
                inicio = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            }

            LocalDateTime fin;
            if (fechaFin != null) {
                fin = java.time.ZonedDateTime.parse(fechaFin).toLocalDateTime();
            } else {
                fin = inicio.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);
            }

            var eventos = calendarioService.obtenerEventosEstudiante(student.getId(), inicio, fin);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener eventos: " + e.getMessage());
        }
    }

    /**
     * Crear un evento personal en el calendario
     */
    @PostMapping("/calendario/eventos")
    @ResponseBody
    public ResponseEntity<?> crearEventoPersonal(
            @RequestBody com.example.demo.dto.EventoCalendarioDTO eventoDTO,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

            var eventoCreado = calendarioService.crearEventoPersonal(student.getId(), eventoDTO);
            return ResponseEntity.ok(eventoCreado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear evento: " + e.getMessage());
        }
    }

    /**
     * Actualizar un evento personal
     */
    @PutMapping("/calendario/eventos/{eventoId}")
    @ResponseBody
    public ResponseEntity<?> actualizarEventoPersonal(
            @PathVariable Long eventoId,
            @RequestBody com.example.demo.dto.EventoCalendarioDTO eventoDTO,
            Authentication authentication) {
        try {
            var eventoActualizado = calendarioService.actualizarEventoPersonal(eventoId, eventoDTO);
            return ResponseEntity.ok(eventoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar evento: " + e.getMessage());
        }
    }

    /**
     * Eliminar un evento personal
     */
    @DeleteMapping("/calendario/eventos/{eventoId}")
    @ResponseBody
    public ResponseEntity<?> eliminarEventoPersonal(
            @PathVariable Long eventoId,
            Authentication authentication) {
        try {
            calendarioService.eliminarEventoPersonal(eventoId);
            return ResponseEntity.ok().body("Evento eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar evento: " + e.getMessage());
        }
    }

    /**
     * Obtener eventos próximos para notificaciones
     */
    @GetMapping("/calendario/proximos")
    @ResponseBody
    public ResponseEntity<?> getEventosProximos(
            @RequestParam(defaultValue = "7") int dias,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentService.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

            var eventosProximos = calendarioService.obtenerEventosProximos(student.getId(), dias);
            return ResponseEntity.ok(eventosProximos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener eventos próximos: " + e.getMessage());
        }
    }

}