package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import com.example.demo.model.StudentCurso;

import com.example.demo.service.AdminService;
import com.example.demo.service.StudentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentCursoService;

import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.ProfessorRepository;
import java.util.Map;
import java.util.Optional;

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
    public String dashboard(Model model) {
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

    // ===========================
    // CRUD STUDENTS
    // ===========================
    @PostMapping("/students")
    @ResponseBody
    public Student createStudent(@RequestBody Student student) {
        return studentService.save(student);
    }

    @GetMapping("/students/{id}")
    @ResponseBody
    public Student getStudent(@PathVariable Long id) {
        return studentService.findById(id).orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));
    }

    @PutMapping("/students/{id}")
    @ResponseBody
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.update(id, student);
    }

    @DeleteMapping("/students/{id}")
    @ResponseBody
    public String deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return "Estudiante eliminado";
    }

    // ===========================
    // CRUD PROFESSORS
    // ===========================
    @PostMapping("/profesores")
    @ResponseBody
    public Professor createProfessor(@RequestBody Professor professor) {
        return professorService.create(professor);
    }

    @GetMapping("/profesores/{id}")
    @ResponseBody
    public Professor getProfessor(@PathVariable Long id) {
        return professorService.getById(id).orElseThrow(() -> new IllegalStateException("Profesor no encontrado"));
    }

    @PutMapping("/profesores/{id}")
    @ResponseBody
    public Professor updateProfessor(@PathVariable Long id, @RequestBody Professor professor) {
        return professorService.update(id, professor);
    }

    @DeleteMapping("/profesores/{id}")
    @ResponseBody
    public String deleteProfessor(@PathVariable Long id) {
        professorService.delete(id);
        return "Profesor eliminado";
    }

    // ===========================
    // CRUD ADMINS
    // ===========================
    @PostMapping("/admins")
    @ResponseBody
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.create(admin);
    }

    @GetMapping("/admins/{id}")
    @ResponseBody
    public Admin getAdmin(@PathVariable Long id) {
        return adminService.findById(id).orElseThrow(() -> new IllegalStateException("Administrador no encontrado"));
    }

    @PutMapping("/admins/{id}")
    @ResponseBody
    public Admin updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        return adminService.update(id, admin);
    }

    @DeleteMapping("/admins/{id}")
    @ResponseBody
    public String deleteAdmin(@PathVariable Long id) {
        adminService.delete(id);
        return "Administrador eliminado";
    }

    // ===========================
    // CRUD CURSOS
    // ===========================
    @PostMapping("/cursos")
    @ResponseBody
    public ResponseEntity<?> createCurso(@RequestBody Map<String, Object> payload) {
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
    public ResponseEntity<Curso> getCurso(@PathVariable Long id) {
        Optional<Curso> curso = cursoRepository.findById(id);
        return curso.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/cursos/update")
    public ResponseEntity<String> updateCurso(@RequestBody Map<String, Object> payload) {
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
    public ResponseEntity<String> deleteCurso(@RequestBody Map<String, Long> payload) {
        try {
            Long cursoId = payload.get("id");
            if (cursoRepository.existsById(cursoId)) {
                cursoRepository.deleteById(cursoId);
                return ResponseEntity.ok("Curso eliminado exitosamente");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Curso no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar curso: " + e.getMessage());
        }
    }

    // ===========================
    // CRUD ASIGNACIONES
    // ===========================
    @PostMapping("/asignaciones")
    @ResponseBody
    public ResponseEntity<?> assignCursoToStudent(@RequestBody Map<String, Long> payload) {
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
    public ResponseEntity<String> unassignCursoFromStudent(@PathVariable Long id) {
        try {
            studentCursoService.unassignCursoFromStudent(id);
            return ResponseEntity.ok("Asignación eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando asignación: " + e.getMessage());
        }
    }

    @GetMapping("/asignaciones/{id}")
    @ResponseBody
    public ResponseEntity<StudentCurso> getAsignacion(@PathVariable Long id) {
        var asignacion = studentCursoService.findById(id);
        return asignacion.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
