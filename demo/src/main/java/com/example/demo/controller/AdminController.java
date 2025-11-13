package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;


import jakarta.validation.Valid;

import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import com.example.demo.model.StudentCurso;
import com.example.demo.model.EstadoAsignacion;

import com.example.demo.service.AdminService;
import com.example.demo.service.StudentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentCursoService;

import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.model.Role;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final StudentCursoService studentCursoService;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private RoleRepository roleRepository;

    public AdminController(AdminService adminService,
                           StudentService studentService,
                           ProfessorService professorService,
                           StudentCursoService studentCursoService,
                           CursoRepository cursoRepository,
                           ProfessorRepository professorRepository) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.professorService = professorService;
        this.studentCursoService = studentCursoService;
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
    }

    // ===========================
    // DASHBOARD
    // ===========================
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Verificar si el usuario tiene permiso para acceder al dashboard admin
        boolean hasAdminPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ACCESS_ADMIN_DASHBOARD"));

        if (!hasAdminPermission) {
            // Usuario no tiene permiso para acceder al dashboard admin
            return "redirect:/error/acceso-denegado";
        }

        var students = studentService.findAll();
        var professors = professorService.findAll();
        var cursos = cursoRepository.findAll();
        var asignaciones = studentCursoService.findAllWithDetails();

        System.out.println("DEBUG: Students count: " + students.size());
        System.out.println("DEBUG: Professors count: " + professors.size());
        System.out.println("DEBUG: Cursos count: " + cursos.size());
        System.out.println("DEBUG: Asignaciones count: " + asignaciones.size());

        for (var curso : cursos) {
            System.out.println("DEBUG: Curso - ID: " + curso.getId() + ", Nombre: " + curso.getNombre() + ", Estado: " + curso.getEstado() + ", Profesor: " + (curso.getProfesor() != null ? curso.getProfesor().getNombre() : "null"));
        }

        model.addAttribute("students", students);
        model.addAttribute("professors", professors);
        // También exponemos la lista con la clave en español porque los templates la usan así
        model.addAttribute("profesores", professors);
        model.addAttribute("admins", adminService.findAll());
        model.addAttribute("cursos", cursos);
        model.addAttribute("asignaciones", asignaciones);
        return "administrador/dashboard";
    }

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint REST para Angular - devuelve todos los datos del dashboard
    @GetMapping("/api/dashboard")
    @ResponseBody
    public Map<String, Object> getDashboardData(HttpServletRequest request) {
        System.out.println("🔍 DEBUG: Endpoint /admin/api/dashboard llamado");
        
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 DEBUG: Authorization header: " + (authHeader != null ? "presente (" + authHeader.length() + " chars)" : "nulo"));
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ DEBUG: Token JWT requerido - formato inválido");
            throw new SecurityException("Token JWT requerido");
        }

        String token = authHeader.substring(7);
        System.out.println("🔍 DEBUG: Token extraído (primeros 50 chars): " + token.substring(0, Math.min(50, token.length())) + "...");
        
        boolean isValid = jwtUtil.validateToken(token);
        System.out.println("🔍 DEBUG: Token válido: " + isValid);
        
        if (!isValid) {
            System.out.println("❌ DEBUG: Token JWT inválido");
            throw new SecurityException("Token JWT inválido");
        }

        // Verificar permisos desde el token JWT
        Map<String, Object> authorities = jwtUtil.getAuthoritiesFromToken(token);
        System.out.println("🔍 DEBUG: Authorities del token: " + authorities);
        
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) authorities.get("permissions");
        System.out.println("🔍 DEBUG: Permissions del token: " + permissions);
        
        boolean hasAdminPermission = permissions != null &&
            permissions.contains("ACCESS_ADMIN_DASHBOARD");
        System.out.println("🔍 DEBUG: Tiene permiso admin: " + hasAdminPermission);

        if (!hasAdminPermission) {
            System.out.println("❌ DEBUG: Acceso denegado - Sin permiso admin");
            throw new SecurityException("Acceso denegado - Permiso admin requerido");
        }
        
        System.out.println("✅ DEBUG: Validación JWT completada exitosamente");

        Map<String, Object> data = new HashMap<>();
        
        var students = studentService.findAll();
        var professors = professorService.findAll();
        var admins = adminService.findAll();
        var cursos = cursoRepository.findAll();
        var asignaciones = studentCursoService.findAllWithDetails();

        data.put("students", students);
        data.put("professors", professors);
        data.put("admins", admins);
        data.put("cursos", cursos);
        data.put("asignaciones", asignaciones);

        // Estadísticas
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", students.size());
        stats.put("totalProfessors", professors.size());
        stats.put("totalAdmins", admins.size());
        stats.put("totalCourses", cursos.size());
        stats.put("totalAssignments", asignaciones.size());
        data.put("stats", stats);

        System.out.println("✅ Angular Dashboard Data - Students: " + students.size() +
                          ", Professors: " + professors.size() +
                          ", Courses: " + cursos.size() +
                          ", Assignments: " + asignaciones.size());

        return data;
    }

    // ===========================
    // ENDPOINTS LISTAR TODOS - PARA ANGULAR
    // ===========================
    
    @GetMapping("/students")
    @ResponseBody
    public List<Student> getAllStudents(HttpServletRequest request) {
        validateJwtTokenFromRequest(request);

        var students = studentService.findAll();
        System.out.println("🔄 GET /admin/students - Total: " + students.size());
        for (var student : students) {
            System.out.println("  - " + student.getNombre() + " (" + student.getCodigo() + ")");
        }
        return students;
    }

    @GetMapping("/profesores")
    @ResponseBody
    public List<Professor> getAllProfessors(HttpServletRequest request) {
        validateJwtTokenFromRequest(request);

        var professors = professorService.findAll();
        System.out.println("🔄 GET /admin/profesores - Total: " + professors.size());
        for (var professor : professors) {
            System.out.println("  - " + professor.getNombre() + " (" + professor.getCodigo() + ") - " + professor.getEspecialidad());
        }
        return professors;
    }

    @GetMapping("/admins")
    @ResponseBody
    public List<Admin> getAllAdmins(HttpServletRequest request) {
        validateJwtTokenFromRequest(request);

        var admins = adminService.findAll();
        System.out.println("🔄 GET /admin/admins - Total: " + admins.size());
        for (var admin : admins) {
            System.out.println("  - " + admin.getNombre() + " (" + admin.getCodigo() + ")");
        }
        return admins;
    }

    @GetMapping("/cursos")
    @ResponseBody
    public List<Curso> getAllCourses(HttpServletRequest request) {
        validateJwtTokenFromRequest(request);

        var cursos = cursoRepository.findAll();
        System.out.println("🔄 GET /admin/cursos - Total: " + cursos.size());
        for (var curso : cursos) {
            System.out.println("  - " + curso.getNombre() + " (" + curso.getCodigo() + ") - Estado: " + curso.getEstado());
            if (curso.getProfesor() != null) {
                System.out.println("    Profesor: " + curso.getProfesor().getNombre());
            }
        }
        return cursos;
    }

    @GetMapping("/asignaciones")
    @ResponseBody
    public List<StudentCurso> getAllAssignments(HttpServletRequest request) {
        validateJwtTokenFromRequest(request);

        var asignaciones = studentCursoService.findAllWithDetails();
        System.out.println("🔄 GET /admin/asignaciones - Total: " + asignaciones.size());
        for (var asignacion : asignaciones) {
            System.out.println("  - " + asignacion.getStudent().getNombre() + " → " +
                              asignacion.getCurso().getNombre() + " - Estado: " + asignacion.getEstado());
        }
        return asignaciones;
    }

    // Método auxiliar para validar JWT token desde HttpServletRequest
    private void validateJwtTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 DEBUG: Validando JWT token desde request...");
        System.out.println("🔍 DEBUG: Auth header recibido: " + (authHeader != null ? "presente (" + authHeader.length() + " chars)" : "nulo"));
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ DEBUG: Token JWT requerido - formato inválido");
            throw new SecurityException("Token JWT requerido");
        }

        String token = authHeader.substring(7);
        System.out.println("🔍 DEBUG: Token extraído (primeros 50 chars): " + token.substring(0, Math.min(50, token.length())) + "...");
        
        boolean isValid = jwtUtil.validateToken(token);
        System.out.println("🔍 DEBUG: Token válido: " + isValid);
        
        if (!isValid) {
            System.out.println("❌ DEBUG: Token JWT inválido");
            throw new SecurityException("Token JWT inválido");
        }

        // Verificar permisos desde el token JWT
        Map<String, Object> authorities = jwtUtil.getAuthoritiesFromToken(token);
        System.out.println("🔍 DEBUG: Authorities del token: " + authorities);
        
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) authorities.get("permissions");
        System.out.println("🔍 DEBUG: Permissions del token: " + permissions);
        
        boolean hasAdminPermission = permissions != null &&
            permissions.contains("ACCESS_ADMIN_DASHBOARD");
        System.out.println("🔍 DEBUG: Tiene permiso admin: " + hasAdminPermission);

        if (!hasAdminPermission) {
            System.out.println("❌ DEBUG: Acceso denegado - Sin permiso admin");
            throw new SecurityException("Acceso denegado - Permiso admin requerido");
        }
        
        System.out.println("✅ DEBUG: Validación JWT completada exitosamente");
    }

    // ===========================
    // CRUD STUDENTS
    // ===========================
    @PostMapping("/students")
    @ResponseBody
    public ResponseEntity<?> createStudent(HttpServletRequest request,
                                           @Valid @RequestBody Student student,
                                           BindingResult bindingResult) {
        validateJwtTokenFromRequest(request);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Asignar rol STUDENT por defecto
            Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalStateException("Rol STUDENT no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(studentRole);
            student.setRoles(roles);
            Student savedStudent = studentService.save(student);
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear estudiante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/students/{id}")
    @ResponseBody
    public Student getStudent(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        return studentService.findById(id).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
    }

    @PutMapping("/students/{id}")
    @ResponseBody
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        return studentService.update(id, student);
    }

    @DeleteMapping("/students/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            // Verificar si el estudiante tiene asignaciones activas
            var asignaciones = studentCursoService.findByStudentId(id);
            boolean hasActiveAsignaciones = asignaciones.stream()
                .anyMatch(a -> a.getEstado() == EstadoAsignacion.ACTIVO);

            if (hasActiveAsignaciones) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No se puede eliminar el estudiante porque tiene cursos asignados activos. Primero desasigne los cursos.");
                return ResponseEntity.badRequest().body(response);
            }

            studentService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estudiante eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar estudiante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===========================
    // CRUD PROFESSORS
    // ===========================
    @PostMapping("/profesores")
    @ResponseBody
    public ResponseEntity<?> createProfessor(HttpServletRequest request,
                                             @Valid @RequestBody Professor professor,
                                             BindingResult bindingResult) {
        validateJwtTokenFromRequest(request);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Asignar rol TEACHER por defecto
            Role teacherRole = roleRepository.findByName("TEACHER")
                .orElseThrow(() -> new IllegalStateException("Rol TEACHER no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(teacherRole);
            professor.setRoles(roles);
            Professor savedProfessor = professorService.create(professor);
            return ResponseEntity.ok(savedProfessor);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear profesor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/profesores/{id}")
    @ResponseBody
    public Professor getProfessor(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        return professorService.getById(id).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));
    }

    @PutMapping("/profesores/{id}")
    @ResponseBody
    public Professor updateProfessor(@PathVariable Long id, @RequestBody Professor professor, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        return professorService.update(id, professor);
    }

    @DeleteMapping("/profesores/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProfessor(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            // Verificar si el profesor tiene cursos asignados
            var cursosAsignados = cursoRepository.findByProfesorId(id);
            if (!cursosAsignados.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No se puede eliminar el profesor porque tiene cursos asignados. Primero reasigne o elimine los cursos.");
                return ResponseEntity.badRequest().body(response);
            }
            professorService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profesor eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar profesor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===========================
    // CRUD ADMINS
    // ===========================
    @PostMapping("/admins")
    @ResponseBody
    public ResponseEntity<?> createAdmin(HttpServletRequest request,
                                         @Valid @RequestBody Admin admin,
                                         BindingResult bindingResult) {
        validateJwtTokenFromRequest(request);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Asignar rol ADMIN por defecto
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);
            Admin savedAdmin = adminService.create(admin);
            return ResponseEntity.ok(savedAdmin);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear administrador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/admins/{id}")
    @ResponseBody
    public Admin getAdmin(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        return adminService.findById(id).orElseThrow(() -> new IllegalStateException("Administrador no encontrado"));
    }

    @PutMapping("/admins/{id}")
    @ResponseBody
    public Admin updateAdmin(@PathVariable Long id, @RequestBody Admin admin, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        return adminService.update(id, admin);
    }

    @DeleteMapping("/admins/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAdmin(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            // Verificar si es el último administrador
            var allAdmins = adminService.findAll();
            if (allAdmins.size() <= 1) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No se puede eliminar el último administrador del sistema.");
                return ResponseEntity.badRequest().body(response);
            }
            adminService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Administrador eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar administrador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===========================
    // CRUD CURSOS
    // ===========================
    @PostMapping("/cursos")
    @ResponseBody
    public ResponseEntity<?> createCurso(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            Curso curso = new Curso();
            curso.setNombre((String) payload.get("nombre"));
            curso.setCodigo((String) payload.get("codigo"));
            curso.setDescripcion((String) payload.get("descripcion"));

            if (payload.get("estado") != null) {
                curso.setEstado(EstadoCurso.valueOf((String) payload.get("estado")));
            }

            if (payload.get("profesor") != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> profesorMap = (Map<String, Object>) payload.get("profesor");
                if (profesorMap.get("id") != null && !profesorMap.get("id").toString().isEmpty()) {
                    Long profesorId = Long.valueOf(profesorMap.get("id").toString());
                    professorRepository.findById(profesorId).ifPresent(curso::setProfesor);
                }
            }

            Curso savedCurso = cursoRepository.save(curso);
            return ResponseEntity.ok(savedCurso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creando curso: " + e.getMessage());
        }
    }

    @GetMapping("/cursos/{id}")
    public ResponseEntity<Curso> getCurso(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        Optional<Curso> curso = cursoRepository.findById(id);
        return curso.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/cursos/update")
    public ResponseEntity<String> updateCurso(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            Long cursoId = Long.valueOf(payload.get("id").toString());
            Optional<Curso> cursoExistenteOpt = cursoRepository.findById(cursoId);

            if (!cursoExistenteOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado");
            }

            Curso curso = cursoExistenteOpt.get();
            curso.setNombre(payload.get("nombre").toString());
            curso.setCodigo(payload.get("codigo").toString());
            curso.setDescripcion(payload.get("descripcion").toString());

            if (payload.get("estado") != null) {
                curso.setEstado(EstadoCurso.valueOf(payload.get("estado").toString()));
            }

            if (payload.get("profesor") != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> profesorMap = (Map<String, Object>) payload.get("profesor");
                if (profesorMap.get("id") != null && !profesorMap.get("id").toString().isEmpty()) {
                    Long profesorId = Long.valueOf(profesorMap.get("id").toString());
                    professorRepository.findById(profesorId).ifPresent(curso::setProfesor);
                } else {
                    curso.setProfesor(null);
                }
            } else {
                curso.setProfesor(null);
            }

            cursoRepository.save(curso);
            return ResponseEntity.ok("Curso actualizado exitosamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar curso: " + e.getMessage());
        }
    }

    @PostMapping("/cursos/delete")
    public ResponseEntity<Map<String, Object>> deleteCurso(@RequestBody Map<String, Long> payload, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            Long cursoId = payload.get("id");
            if (cursoRepository.existsById(cursoId)) {
                // Verificar si el curso tiene estudiantes asignados activos
                var asignacionesActivas = studentCursoService.findAll().stream()
                    .filter(a -> a.getCurso().getId().equals(cursoId) && a.getEstado() == EstadoAsignacion.ACTIVO)
                    .findAny();

                if (asignacionesActivas.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No se puede eliminar el curso porque tiene estudiantes asignados activos. Primero desasigne los estudiantes.");
                    return ResponseEntity.badRequest().body(response);
                }

                cursoRepository.deleteById(cursoId);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Curso eliminado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Curso no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar curso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===========================
    // CRUD ASIGNACIONES
    // ===========================
    @PostMapping("/asignaciones")
    @ResponseBody
    public ResponseEntity<?> assignCursoToStudent(@RequestBody Map<String, Long> payload, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            Long studentId = payload.get("studentId");
            Long cursoId = payload.get("cursoId");
            var asignacion = studentCursoService.assignCursoToStudent(studentId, cursoId);
            return ResponseEntity.ok(asignacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error asignando curso: " + e.getMessage());
        }
    }

    @DeleteMapping("/asignaciones/{id}")
    @ResponseBody
    public ResponseEntity<String> unassignCursoFromStudent(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        try {
            studentCursoService.unassignCursoFromStudent(id);
            return ResponseEntity.ok("Asignación eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando asignación: " + e.getMessage());
        }
    }

    @GetMapping("/asignaciones/{id}")
    @ResponseBody
    public ResponseEntity<StudentCurso> getAsignacion(@PathVariable Long id, HttpServletRequest request) {
        validateJwtTokenFromRequest(request);
        var asignacion = studentCursoService.findById(id);
        return asignacion.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
