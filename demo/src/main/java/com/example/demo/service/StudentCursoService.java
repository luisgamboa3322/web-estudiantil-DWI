package com.example.demo.service;

import com.example.demo.model.StudentCurso;
import com.example.demo.model.EstadoAsignacion;
import com.example.demo.repository.StudentCursoRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.CursoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentCursoService {

    private final StudentCursoRepository studentCursoRepository;
    private final StudentRepository studentRepository;
    private final CursoRepository cursoRepository;

    public StudentCursoService(StudentCursoRepository studentCursoRepository,
                               StudentRepository studentRepository,
                               CursoRepository cursoRepository) {
        this.studentCursoRepository = studentCursoRepository;
        this.studentRepository = studentRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<StudentCurso> findAll() {
        return studentCursoRepository.findAll();
    }

    public List<StudentCurso> findAllWithDetails() {
        return studentCursoRepository.findAllWithDetails();
    }

    public Optional<StudentCurso> findById(Long id) {
        return studentCursoRepository.findById(id);
    }

    public StudentCurso assignCursoToStudent(Long studentId, Long cursoId) {
        // Validar que el estudiante existe
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado"));

        // Validar que el curso existe y tiene profesor asignado
        var curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalStateException("Curso no encontrado"));

        if (curso.getProfesor() == null) {
            throw new IllegalStateException("El curso debe tener un profesor asignado");
        }

        // Verificar que no esté ya asignado
        boolean alreadyAssigned = studentCursoRepository.findByStudentId(studentId)
                .stream()
                .anyMatch(sc -> sc.getCurso().getId().equals(cursoId) && sc.getEstado() == EstadoAsignacion.ACTIVO);

        if (alreadyAssigned) {
            throw new IllegalStateException("El curso ya está asignado a este estudiante");
        }

        StudentCurso asignacion = new StudentCurso();
        asignacion.setStudent(student);
        asignacion.setCurso(curso);
        asignacion.setEstado(EstadoAsignacion.ACTIVO);

        return studentCursoRepository.save(asignacion);
    }

    public void unassignCursoFromStudent(Long asignacionId) {
        StudentCurso asignacion = studentCursoRepository.findById(asignacionId)
                .orElseThrow(() -> new IllegalStateException("Asignación no encontrada"));

        // En lugar de eliminar, cambiar estado a INACTIVO
        asignacion.setEstado(EstadoAsignacion.INACTIVO);
        studentCursoRepository.save(asignacion);
    }

    public List<StudentCurso> findByStudentId(Long studentId) {
        return studentCursoRepository.findByStudentId(studentId);
    }

    public boolean hasActiveAsignacionesByStudentId(Long studentId) {
        return studentCursoRepository.existsByStudentIdAndEstado(studentId, EstadoAsignacion.ACTIVO);
    }

    public boolean hasActiveAsignacionesByCursoId(Long cursoId) {
        return studentCursoRepository.existsByCursoIdAndEstado(cursoId, EstadoAsignacion.ACTIVO);
    }
}