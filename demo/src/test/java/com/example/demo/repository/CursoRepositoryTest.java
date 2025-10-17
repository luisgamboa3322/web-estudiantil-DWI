package com.example.demo.repository;

import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import com.example.demo.model.Professor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CursoRepositoryTest {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Test
    void shouldSaveAndFindCurso() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professor = professorRepository.save(professor);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101");
        curso.setDescripcion("Curso introductorio de matemáticas");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(professor);

        // When
        Curso savedCurso = cursoRepository.save(curso);

        // Then
        assertThat(savedCurso.getId()).isNotNull();
        assertThat(savedCurso.getNombre()).isEqualTo("Matemáticas Básicas");
        assertThat(savedCurso.getCodigo()).isEqualTo("MAT101");
        assertThat(savedCurso.getEstado()).isEqualTo(EstadoCurso.ACTIVO);
        assertThat(savedCurso.getProfesor()).isNotNull();
        assertThat(savedCurso.getProfesor().getId()).isEqualTo(professor.getId());
    }

    @Test
    void shouldFindCursoByCodigo() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professor = professorRepository.save(professor);

        Curso curso = new Curso();
        curso.setNombre("Matemáticas Básicas");
        curso.setCodigo("MAT101");
        curso.setDescripcion("Curso introductorio de matemáticas");
        curso.setEstado(EstadoCurso.ACTIVO);
        curso.setProfesor(professor);
        cursoRepository.save(curso);

        // When
        Optional<Curso> foundCurso = cursoRepository.findByCodigo("MAT101");

        // Then
        assertThat(foundCurso).isPresent();
        assertThat(foundCurso.get().getCodigo()).isEqualTo("MAT101");
    }

    @Test
    void shouldFindCursosByEstado() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professor = professorRepository.save(professor);

        Curso curso1 = new Curso();
        curso1.setNombre("Matemáticas Básicas");
        curso1.setCodigo("MAT101");
        curso1.setDescripcion("Curso introductorio");
        curso1.setEstado(EstadoCurso.ACTIVO);
        curso1.setProfesor(professor);

        Curso curso2 = new Curso();
        curso2.setNombre("Física Básica");
        curso2.setCodigo("FIS101");
        curso2.setDescripcion("Curso introductorio");
        curso2.setEstado(EstadoCurso.INACTIVO);
        curso2.setProfesor(professor);

        cursoRepository.save(curso1);
        cursoRepository.save(curso2);

        // When
        List<Curso> activeCursos = cursoRepository.findByEstado("ACTIVO");

        // Then
        assertThat(activeCursos).hasSizeGreaterThanOrEqualTo(1);
        assertThat(activeCursos.stream().allMatch(c -> c.getEstado().toString().equals("ACTIVO"))).isTrue();
    }

    @Test
    void shouldFindCursosByProfesorId() {
        // Given
        Professor professor1 = new Professor();
        professor1.setNombre("Professor 1");
        professor1.setCodigo("PROF001");
        professor1.setEmail("professor1@test.com");
        professor1.setEspecialidad("Matemáticas");
        professor1.setPassword("password123");
        Professor savedProfessor1 = professorRepository.save(professor1);

        Professor professor2 = new Professor();
        professor2.setNombre("Professor 2");
        professor2.setCodigo("PROF002");
        professor2.setEmail("professor2@test.com");
        professor2.setEspecialidad("Física");
        professor2.setPassword("password123");
        professorRepository.save(professor2);

        Curso curso1 = new Curso();
        curso1.setNombre("Matemáticas Básicas");
        curso1.setCodigo("MAT101");
        curso1.setDescripcion("Curso introductorio");
        curso1.setEstado(EstadoCurso.ACTIVO);
        curso1.setProfesor(savedProfessor1);

        Curso curso2 = new Curso();
        curso2.setNombre("Matemáticas Avanzadas");
        curso2.setCodigo("MAT201");
        curso2.setDescripcion("Curso avanzado");
        curso2.setEstado(EstadoCurso.ACTIVO);
        curso2.setProfesor(savedProfessor1);

        Curso curso3 = new Curso();
        curso3.setNombre("Física Básica");
        curso3.setCodigo("FIS101");
        curso3.setDescripcion("Curso introductorio");
        curso3.setEstado(EstadoCurso.ACTIVO);
        curso3.setProfesor(professor2);

        cursoRepository.save(curso1);
        cursoRepository.save(curso2);
        cursoRepository.save(curso3);

        // When
        List<Curso> cursosProf1 = cursoRepository.findByProfesorId(savedProfessor1.getId());

        // Then
        assertThat(cursosProf1).hasSize(2);
        assertThat(cursosProf1.stream().allMatch(c -> c.getProfesor().getId().equals(savedProfessor1.getId()))).isTrue();
    }

    @Test
    void shouldFindAllCursos() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professor = professorRepository.save(professor);

        Curso curso1 = new Curso();
        curso1.setNombre("Matemáticas Básicas");
        curso1.setCodigo("MAT101");
        curso1.setDescripcion("Curso introductorio");
        curso1.setEstado(EstadoCurso.ACTIVO);
        curso1.setProfesor(professor);

        Curso curso2 = new Curso();
        curso2.setNombre("Física Básica");
        curso2.setCodigo("FIS101");
        curso2.setDescripcion("Curso introductorio");
        curso2.setEstado(EstadoCurso.ACTIVO);
        curso2.setProfesor(professor);

        cursoRepository.save(curso1);
        cursoRepository.save(curso2);

        // When
        var cursos = cursoRepository.findAll();

        // Then
        assertThat(cursos).hasSizeGreaterThanOrEqualTo(2);
    }
}