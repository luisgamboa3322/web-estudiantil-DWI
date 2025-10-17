package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.repository.StudentCursoRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentCursoServiceTest {

    @Autowired
    private StudentCursoService studentCursoService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentCursoRepository studentCursoRepository;

    @Test
    void shouldAssignCursoToStudentSuccessfully() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.nanoTime());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        // When
        StudentCurso result = studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStudent().getId()).isEqualTo(savedStudent.getId());
        assertThat(result.getCurso().getId()).isEqualTo(savedCurso.getId());
        assertThat(result.getEstado()).isEqualTo(EstadoAsignacion.ACTIVO);
        assertThat(result.getFechaAsignacion()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenAssigningToNonExistentStudent() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.nanoTime());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        // When & Then
        assertThatThrownBy(() -> studentCursoService.assignCursoToStudent(999L, savedCurso.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Estudiante no encontrado");
    }

    @Test
    void shouldThrowExceptionWhenAssigningToNonExistentCurso() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        // When & Then
        assertThatThrownBy(() -> studentCursoService.assignCursoToStudent(savedStudent.getId(), 999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Curso no encontrado");
    }

    @Test
    void shouldThrowExceptionWhenAssigningCursoWithoutProfessor() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101");
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        // No profesor assigned
        Curso savedCurso = cursoRepository.save(curso);

        // When & Then
        assertThatThrownBy(() -> studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El curso debe tener un profesor asignado");
    }

    @Test
    void shouldThrowExceptionWhenAssigningAlreadyAssignedCurso() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.nanoTime());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        // First assignment
        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // Second assignment (should fail)
        // When & Then
        assertThatThrownBy(() -> studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El curso ya está asignado a este estudiante");
    }

    @Test
    void shouldUnassignCursoFromStudent() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.currentTimeMillis());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        StudentCurso assignment = studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // When
        studentCursoService.unassignCursoFromStudent(assignment.getId());

        // Then
        Optional<StudentCurso> updatedAssignment = studentCursoService.findById(assignment.getId());
        assertThat(updatedAssignment).isPresent();
        assertThat(updatedAssignment.get().getEstado()).isEqualTo(EstadoAsignacion.INACTIVO);
    }

    @Test
    void shouldThrowExceptionWhenUnassigningNonExistentAssignment() {
        // When & Then
        assertThatThrownBy(() -> studentCursoService.unassignCursoFromStudent(999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Asignación no encontrada");
    }

    @Test
    void shouldFindByStudentId() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso1 = new Curso();
        curso1.setNombre("Matemáticas Básicas");
        curso1.setCodigo("MAT101");
        curso1.setDescripcion("Curso introductorio");
        curso1.setEstado(EstadoCurso.ACTIVO);
        curso1.setProfesor(savedProfessor);
        Curso savedCurso1 = cursoRepository.save(curso1);

        Curso curso2 = new Curso();
        curso2.setNombre("Física Básica");
        curso2.setCodigo("FIS101");
        curso2.setDescripcion("Curso introductorio");
        curso2.setEstado(EstadoCurso.ACTIVO);
        curso2.setProfesor(savedProfessor);
        Curso savedCurso2 = cursoRepository.save(curso2);

        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso1.getId());
        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso2.getId());

        // When
        List<StudentCurso> assignments = studentCursoService.findByStudentId(savedStudent.getId());

        // Then
        assertThat(assignments).hasSize(2);
        assertThat(assignments.stream().allMatch(a -> a.getStudent().getId().equals(savedStudent.getId()))).isTrue();
    }

    @Test
    void shouldCheckHasActiveAsignacionesByStudentId() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.nanoTime());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // When & Then
        assertThat(studentCursoService.hasActiveAsignacionesByStudentId(savedStudent.getId())).isTrue();
        assertThat(studentCursoService.hasActiveAsignacionesByStudentId(999L)).isFalse();
    }

    @Test
    void shouldCheckHasActiveAsignacionesByCursoId() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.nanoTime());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // When & Then
        assertThat(studentCursoService.hasActiveAsignacionesByCursoId(savedCurso.getId())).isTrue();
        assertThat(studentCursoService.hasActiveAsignacionesByCursoId(999L)).isFalse();
    }

    @Test
    void shouldFindAllStudentCursos() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101-" + System.nanoTime());
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // When
        List<StudentCurso> allAssignments = studentCursoService.findAll();

        // Then
        assertThat(allAssignments).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldFindAllWithDetails() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentRepository.save(student);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101");
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        studentCursoService.assignCursoToStudent(savedStudent.getId(), savedCurso.getId());

        // When
        List<StudentCurso> assignmentsWithDetails = studentCursoService.findAllWithDetails();

        // Then
        assertThat(assignmentsWithDetails).hasSizeGreaterThanOrEqualTo(1);
        StudentCurso assignment = assignmentsWithDetails.get(0);
        assertThat(assignment.getStudent()).isNotNull();
        assertThat(assignment.getCurso()).isNotNull();
        assertThat(assignment.getCurso().getProfesor()).isNotNull();
    }
}