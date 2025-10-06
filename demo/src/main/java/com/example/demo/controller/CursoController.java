package com.example.demo.controller;

import com.example.demo.model.Curso;
import com.example.demo.model.Professor;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    // Obtener todos los cursos
    @GetMapping("/cursos")
    public List<Curso> getAllCursos() {
        return cursoRepository.findAll();
    }

    // Obtener cursos por profesor
    @GetMapping("/cursos/profesor/{profesorId}")
    public List<Curso> getCursosByProfesor(@PathVariable Long profesorId) {
        return cursoRepository.findByProfesorId(profesorId);
    }

    // Crear nuevo curso
    @PostMapping("/cursos")
    public ResponseEntity<?> createCurso(@RequestBody Curso curso) {
        try {
            // Asignación de profesor
            if (curso.getProfesor() != null && curso.getProfesor().getId() != null) {
                Optional<Professor> profesorOpt = professorRepository.findById(curso.getProfesor().getId());
                profesorOpt.ifPresent(curso::setProfesor);
            } else {
                curso.setProfesor(null);
            }
            
            Curso savedCurso = cursoRepository.save(curso);
            return ResponseEntity.ok(savedCurso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creando curso: " + e.getMessage());
        }
    }

    // Actualizar curso
    @PutMapping("/cursos/{id}")
    public ResponseEntity<?> updateCurso(@PathVariable Long id, @RequestBody Curso cursoDetails) {
        try {
            Optional<Curso> cursoOpt = cursoRepository.findById(id);
            if (cursoOpt.isPresent()) {
                Curso curso = cursoOpt.get();
                curso.setNombre(cursoDetails.getNombre());
                curso.setCodigo(cursoDetails.getCodigo());
                curso.setDescripcion(cursoDetails.getDescripcion());
                curso.setEstado(cursoDetails.getEstado());
                
                // Asignación de profesor
                if (cursoDetails.getProfesor() != null && cursoDetails.getProfesor().getId() != null) {
                    Optional<Professor> profesorOpt = professorRepository.findById(cursoDetails.getProfesor().getId());
                    profesorOpt.ifPresent(curso::setProfesor);
                } else {
                    curso.setProfesor(null);
                }
                
                Curso updatedCurso = cursoRepository.save(curso);
                return ResponseEntity.ok(updatedCurso);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error actualizando curso: " + e.getMessage());
        }
    }

    // Eliminar curso
    @DeleteMapping("/cursos/{id}")
    public ResponseEntity<?> deleteCurso(@PathVariable Long id) {
        try {
            cursoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error eliminando curso: " + e.getMessage());
        }
    }
}
