package com.example.demo.repository;

import com.example.demo.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentCursoRepositoryTest {

    @Autowired
    private StudentCursoRepository studentCursoRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Test
    void shouldSaveAndFindStudentCurso() {
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

        StudentCurso studentCurso = new StudentCurso();
        studentCurso.setStudent(savedStudent);
        studentCurso.setCurso(savedCurso);
        studentCurso.setEstado(EstadoAsignacion.ACTIVO);

        // When
        StudentCurso savedStudentCurso = studentCursoRepository.save(studentCurso);

        // Then
        assertThat(savedStudentCurso.getId()).isNotNull();
        assertThat(savedStudentCurso.getStudent().getId()).isEqualTo(savedStudent.getId());
        assertThat(savedStudentCurso.getCurso().getId()).isEqualTo(savedCurso.getId());
        assertThat(savedStudentCurso.getEstado()).isEqualTo(EstadoAsignacion.ACTIVO);
        assertThat(savedStudentCurso.getFechaAsignacion()).isNotNull();
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

        Student student1 = new Student();
        student1.setNombre("Student 1");
        student1.setCodigo("STU001");
        student1.setEmail("student1@test.com");
        student1.setPassword("password123");
        Student savedStudent1 = studentRepository.save(student1);

        Student student2 = new Student();
        student2.setNombre("Student 2");
        student2.setCodigo("STU002");
        student2.setEmail("student2@test.com");
        student2.setPassword("password123");
        Student savedStudent2 = studentRepository.save(student2);

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

        StudentCurso studentCurso1 = new StudentCurso();
        studentCurso1.setStudent(savedStudent1);
        studentCurso1.setCurso(savedCurso1);
        studentCurso1.setEstado(EstadoAsignacion.ACTIVO);

        StudentCurso studentCurso2 = new StudentCurso();
        studentCurso2.setStudent(savedStudent1);
        studentCurso2.setCurso(savedCurso2);
        studentCurso2.setEstado(EstadoAsignacion.ACTIVO);

        StudentCurso studentCurso3 = new StudentCurso();
        studentCurso3.setStudent(savedStudent2);
        studentCurso3.setCurso(savedCurso1);
        studentCurso3.setEstado(EstadoAsignacion.ACTIVO);

        studentCursoRepository.save(studentCurso1);
        studentCursoRepository.save(studentCurso2);
        studentCursoRepository.save(studentCurso3);

        // When
        List<StudentCurso> studentCursos = studentCursoRepository.findByStudentId(savedStudent1.getId());

        // Then
        assertThat(studentCursos).hasSize(2);
        assertThat(studentCursos.stream().allMatch(sc -> sc.getStudent().getId().equals(savedStudent1.getId()))).isTrue();
    }

    @Test
    void shouldFindByCursoId() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor savedProfessor = professorRepository.save(professor);

        Student student1 = new Student();
        student1.setNombre("Student 1");
        student1.setCodigo("STU001");
        student1.setEmail("student1@test.com");
        student1.setPassword("password123");
        Student savedStudent1 = studentRepository.save(student1);

        Student student2 = new Student();
        student2.setNombre("Student 2");
        student2.setCodigo("STU002");
        student2.setEmail("student2@test.com");
        student2.setPassword("password123");
        Student savedStudent2 = studentRepository.save(student2);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101");
        curso.setDescripcion("Curso introductorio");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(savedProfessor);
        Curso savedCurso = cursoRepository.save(curso);

        StudentCurso studentCurso1 = new StudentCurso();
        studentCurso1.setStudent(savedStudent1);
        studentCurso1.setCurso(savedCurso);
        studentCurso1.setEstado(EstadoAsignacion.ACTIVO);

        StudentCurso studentCurso2 = new StudentCurso();
        studentCurso2.setStudent(savedStudent2);
        studentCurso2.setCurso(savedCurso);
        studentCurso2.setEstado(EstadoAsignacion.ACTIVO);

        studentCursoRepository.save(studentCurso1);
        studentCursoRepository.save(studentCurso2);

        // When
        List<StudentCurso> studentCursos = studentCursoRepository.findByCursoId(savedCurso.getId());

        // Then
        assertThat(studentCursos).hasSize(2);
        assertThat(studentCursos.stream().allMatch(sc -> sc.getCurso().getId().equals(savedCurso.getId()))).isTrue();
    }

    @Test
    void shouldFindByEstado() {
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

        StudentCurso studentCurso1 = new StudentCurso();
        studentCurso1.setStudent(savedStudent);
        studentCurso1.setCurso(savedCurso);
        studentCurso1.setEstado(EstadoAsignacion.ACTIVO);

        StudentCurso studentCurso2 = new StudentCurso();
        studentCurso2.setStudent(savedStudent);
        studentCurso2.setCurso(savedCurso);
        studentCurso2.setEstado(EstadoAsignacion.INACTIVO);

        studentCursoRepository.save(studentCurso1);
        studentCursoRepository.save(studentCurso2);

        // When
        List<StudentCurso> activeStudentCursos = studentCursoRepository.findByEstado("ACTIVO");

        // Then
        assertThat(activeStudentCursos).hasSizeGreaterThanOrEqualTo(1);
        assertThat(activeStudentCursos.stream().allMatch(sc -> sc.getEstado().toString().equals("ACTIVO"))).isTrue();
    }

    @Test
    void shouldExistsByStudentIdAndEstado() {
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

        StudentCurso studentCurso = new StudentCurso();
        studentCurso.setStudent(savedStudent);
        studentCurso.setCurso(savedCurso);
        studentCurso.setEstado(EstadoAsignacion.ACTIVO);
        studentCursoRepository.save(studentCurso);

        // When & Then
        assertThat(studentCursoRepository.existsByStudentIdAndEstado(savedStudent.getId(), EstadoAsignacion.ACTIVO)).isTrue();
        assertThat(studentCursoRepository.existsByStudentIdAndEstado(savedStudent.getId(), EstadoAsignacion.INACTIVO)).isFalse();
    }

    @Test
    void shouldExistsByCursoIdAndEstado() {
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

        StudentCurso studentCurso = new StudentCurso();
        studentCurso.setStudent(savedStudent);
        studentCurso.setCurso(savedCurso);
        studentCurso.setEstado(EstadoAsignacion.ACTIVO);
        studentCursoRepository.save(studentCurso);

        // When & Then
        assertThat(studentCursoRepository.existsByCursoIdAndEstado(savedCurso.getId(), EstadoAsignacion.ACTIVO)).isTrue();
        assertThat(studentCursoRepository.existsByCursoIdAndEstado(savedCurso.getId(), EstadoAsignacion.INACTIVO)).isFalse();
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

        StudentCurso studentCurso = new StudentCurso();
        studentCurso.setStudent(savedStudent);
        studentCurso.setCurso(savedCurso);
        studentCurso.setEstado(EstadoAsignacion.ACTIVO);
        studentCursoRepository.save(studentCurso);

        // When
        List<StudentCurso> studentCursosWithDetails = studentCursoRepository.findAllWithDetails();

        // Then
        assertThat(studentCursosWithDetails).hasSizeGreaterThanOrEqualTo(1);
        StudentCurso result = studentCursosWithDetails.get(0);
        assertThat(result.getStudent()).isNotNull();
        assertThat(result.getCurso()).isNotNull();
        assertThat(result.getCurso().getProfesor()).isNotNull();
    }
}