package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Student;
import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import com.example.demo.service.StudentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.ProfessorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final ProfessorService professorService;
   @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private ProfessorRepository profesorRepository;


    public AdminController(StudentService studentService, 
                         ProfessorService professorService,
                         CursoRepository cursoRepository,
                         ProfessorRepository professorRepository) {
        this.studentService = studentService;
        this.professorService = professorService;
        this.cursoRepository = cursoRepository;
        this.profesorRepository = professorRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Student> students = studentService.findAll();
        List<Professor> professors = professorService.findAll();
        List<Curso> cursos = cursoRepository.findAll();
        
        model.addAttribute("students", students);
        model.addAttribute("professors", professors);
        model.addAttribute("cursos", cursos); // Para la tabla de cursos
        model.addAttribute("profesores", professors); // Para el select del modal
        
        return "administrador/dashboard";
    }

    // Endpoint para crear estudiante (JSON)
    @PostMapping("/students")
    @ResponseBody
    public Student createStudent(@RequestBody Student student) {
        return studentService.save(student);
    }

    // Endpoint para crear profesor (JSON)
    @PostMapping("/profesores")
    @ResponseBody
    public Professor createProfessor(@RequestBody Professor professor) {
        return professorService.create(professor);
    }

    // ✅ NUEVO: Endpoint para crear curso (JSON)
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
        
        // Manejar profesor - FORMA CORRECTA
        if (payload.get("profesor") != null) {
            Map<String, Object> profesorMap = (Map<String, Object>) payload.get("profesor");
            if (profesorMap.get("id") != null && !profesorMap.get("id").toString().isEmpty()) {
                Long profesorId = Long.valueOf(profesorMap.get("id").toString());
                Optional<Professor> profesorOpt = profesorRepository.findById(profesorId);
                if (profesorOpt.isPresent()) {
                    curso.setProfesor(profesorOpt.get());
                }
            }
        }
        
        Curso savedCurso = cursoRepository.save(curso);
        return ResponseEntity.ok(savedCurso);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Error creando curso: " + e.getMessage());
    }
}

    // ✅ NUEVO: Endpoint para obtener todos los cursos (JSON)
    
    // Obtener curso por ID para editar
    @GetMapping("/cursos/{id}")
    public ResponseEntity<Curso> getCurso(@PathVariable Long id) {
        Optional<Curso> curso = cursoRepository.findById(id);
        return curso.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar curso
    // En AdminController.java - REEMPLAZA el método updateCurso actual
@PutMapping("/cursos/update")
public ResponseEntity<String> updateCurso(@RequestBody Map<String, Object> payload) {
    try {
        Long cursoId = Long.valueOf(payload.get("id").toString());
        Optional<Curso> cursoExistenteOpt = cursoRepository.findById(cursoId);
        
        if (!cursoExistenteOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Curso no encontrado");
        }
        
        Curso cursoExistente = cursoExistenteOpt.get();
        
        // Actualizar campos básicos
        cursoExistente.setNombre(payload.get("nombre").toString());
        cursoExistente.setCodigo(payload.get("codigo").toString());
        cursoExistente.setDescripcion(payload.get("descripcion").toString());
        
        // Manejar el estado
        if (payload.get("estado") != null) {
            cursoExistente.setEstado(EstadoCurso.valueOf(payload.get("estado").toString()));
        }
        
        // Manejar el profesor - FORMA CORRECTA
        if (payload.get("profesor") != null) {
            Map<String, Object> profesorMap = (Map<String, Object>) payload.get("profesor");
            if (profesorMap.get("id") != null && !profesorMap.get("id").toString().isEmpty()) {
                Long profesorId = Long.valueOf(profesorMap.get("id").toString());
                Optional<Professor> profesorOpt = profesorRepository.findById(profesorId);
                if (profesorOpt.isPresent()) {
                    cursoExistente.setProfesor(profesorOpt.get());
                } else {
                    cursoExistente.setProfesor(null);
                }
            } else {
                cursoExistente.setProfesor(null);
            }
        } else {
            cursoExistente.setProfesor(null);
        }
        
        cursoRepository.save(cursoExistente);
        return ResponseEntity.ok("Curso actualizado exitosamente");
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error al actualizar curso: " + e.getMessage());
    }
}

    // Eliminar curso (ya lo tienes)
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
}