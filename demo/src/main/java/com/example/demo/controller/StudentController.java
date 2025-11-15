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

    // ===========================
    // API PARA GESTIÓN DE EVALUACIONES (ESTUDIANTE)
    // ===========================
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

        return evaluacionService.findBySemanaId(semanaId);
    }

    @PostMapping("/evaluaciones/{evaluacionId}/iniciar")
    @ResponseBody
    public ResponseEntity<?> iniciarIntento(@PathVariable Long evaluacionId, Authentication authentication) {
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

            // Verificar que la evaluación pertenece a un curso asignado al estudiante
            Evaluacion evaluacion = evaluacionService.findById(evaluacionId).orElse(null);
            if (evaluacion == null) {
                return ResponseEntity.status(404).body("Evaluación no encontrada");
            }

            boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(evaluacion.getSemana().getCurso().getId())
                          && sc.getEstado() == EstadoAsignacion.ACTIVO);

            if (!estaAsignado) {
                return ResponseEntity.status(403).body("No tienes acceso a esta evaluación");
            }

            IntentoEvaluacion intento = evaluacionService.iniciarIntento(evaluacionId, student.getId());
            return ResponseEntity.ok(intento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error iniciando intento: " + e.getMessage());
        }
    }

    @GetMapping("/intentos/{intentoId}")
    @ResponseBody
    public ResponseEntity<?> getIntento(@PathVariable Long intentoId, Authentication authentication) {
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

            // Buscar el intento (necesitamos buscar en todas las evaluaciones)
            // Por simplicidad, asumimos que el intento pertenece al estudiante
            // En producción, deberías tener un método en el servicio para buscar por ID
            return ResponseEntity.ok().body("Intento encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error obteniendo intento: " + e.getMessage());
        }
    }

    @PostMapping("/intentos/{intentoId}/respuestas")
    @ResponseBody
    public ResponseEntity<?> guardarRespuesta(@PathVariable Long intentoId, @RequestBody Map<String, Object> payload, Authentication authentication) {
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

            Long preguntaId = Long.parseLong(payload.get("preguntaId").toString());
            String respuestaTexto = (String) payload.get("respuestaTexto");
            Long opcionSeleccionadaId = payload.get("opcionSeleccionadaId") != null ? 
                Long.parseLong(payload.get("opcionSeleccionadaId").toString()) : null;
            String opcionesOrdenadas = (String) payload.get("opcionesOrdenadas");

            RespuestaEstudiante respuesta = evaluacionService.guardarRespuesta(intentoId, preguntaId, 
                respuestaTexto, opcionSeleccionadaId, opcionesOrdenadas);
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error guardando respuesta: " + e.getMessage());
        }
    }

    @PostMapping("/intentos/{intentoId}/finalizar")
    @ResponseBody
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

            IntentoEvaluacion intento = evaluacionService.finalizarIntento(intentoId);
            return ResponseEntity.ok(intento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error finalizando intento: " + e.getMessage());
        }
    }

    @GetMapping("/evaluaciones/{evaluacionId}/intentos")
    @ResponseBody
    public List<IntentoEvaluacion> getMisIntentos(@PathVariable Long evaluacionId, Authentication authentication) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return new ArrayList<>();
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElse(null);

        if (student == null) return new ArrayList<>();

        return evaluacionService.getIntentosByEvaluacionIdAndEstudianteId(evaluacionId, student.getId());
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
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        IntentoEvaluacion intento = evaluacionService.findIntentoById(intentoId)
            .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));

        // Verificar que el intento pertenece al estudiante
        if (!intento.getEstudiante().getId().equals(student.getId())) {
            return "redirect:/error/acceso-denegado";
        }

        // Verificar que el intento está en progreso
        if (intento.isCompletado() || intento.isExpirado()) {
            return "redirect:/student/evaluacion/resultados/" + intento.getEvaluacion().getId();
        }

        // Cargar preguntas con sus opciones
        List<Pregunta> preguntas = evaluacionService.getPreguntasByEvaluacionId(intento.getEvaluacion().getId());
        // Cargar opciones para cada pregunta e inicializar para serialización
        for (Pregunta pregunta : preguntas) {
            List<OpcionRespuesta> opciones = evaluacionService.getOpcionesByPreguntaId(pregunta.getId());
            pregunta.setOpciones(opciones);
            // Inicializar opciones para asegurar serialización correcta
            Hibernate.initialize(pregunta.getOpciones());
        }
        
        // Inicializar relaciones lazy de Evaluacion para evitar errores de serialización
        Evaluacion evaluacion = intento.getEvaluacion();
        Hibernate.initialize(evaluacion.getSemana());
        Hibernate.initialize(evaluacion.getProfesor());
        
        model.addAttribute("intento", intento);
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("studentName", student.getNombre());

        return "student/realizar-evaluacion";
    }

    @GetMapping("/evaluacion/resultados/{evaluacionId}")
    public String verResultados(@PathVariable Long evaluacionId, Authentication authentication, Model model) {
        // Verificar permisos
        boolean hasStudentPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_STUDENT_DASHBOARD"));
        if (!hasStudentPermission) {
            return "redirect:/error/acceso-denegado";
        }

        String email = authentication.getName();
        Student student = studentService.findByEmail(email).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        Evaluacion evaluacion = evaluacionService.findById(evaluacionId)
            .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        // Verificar que el estudiante está asignado al curso
        boolean estaAsignado = studentCursoService.findByStudentId(student.getId()).stream()
            .anyMatch(sc -> sc.getCurso().getId().equals(evaluacion.getSemana().getCurso().getId())
                      && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (!estaAsignado) {
            return "redirect:/error/acceso-denegado";
        }

        List<IntentoEvaluacion> intentos = evaluacionService.getIntentosByEvaluacionIdAndEstudianteId(evaluacionId, student.getId());

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("intentos", intentos);
        model.addAttribute("studentName", student.getNombre());

        return "student/resultados-evaluacion";
    }

}