package com.example.demo.repository;

import com.example.demo.model.Professor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProfessorRepositoryTest {

    @Autowired
    private ProfessorRepository professorRepository;

    @Test
    void shouldSaveAndFindProfessor() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");

        // When
        Professor savedProfessor = professorRepository.save(professor);

        // Then
        assertThat(savedProfessor.getId()).isNotNull();
        assertThat(savedProfessor.getNombre()).isEqualTo("Professor Test");
        assertThat(savedProfessor.getCodigo()).isEqualTo("PROF001");
        assertThat(savedProfessor.getEmail()).isEqualTo("professor@test.com");
        assertThat(savedProfessor.getEspecialidad()).isEqualTo("Matemáticas");
    }

    @Test
    void shouldFindProfessorByEmail() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professorRepository.save(professor);

        // When
        Optional<Professor> foundProfessor = professorRepository.findByEmail("professor@test.com");

        // Then
        assertThat(foundProfessor).isPresent();
        assertThat(foundProfessor.get().getEmail()).isEqualTo("professor@test.com");
    }

    @Test
    void shouldFindProfessorByCodigo() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professorRepository.save(professor);

        // When
        Optional<Professor> foundProfessor = professorRepository.findByCodigo("PROF001");

        // Then
        assertThat(foundProfessor).isPresent();
        assertThat(foundProfessor.get().getCodigo()).isEqualTo("PROF001");
    }

    @Test
    void shouldCheckExistsByCodigo() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professorRepository.save(professor);

        // When & Then
        assertThat(professorRepository.existsByCodigo("PROF001")).isTrue();
        assertThat(professorRepository.existsByCodigo("PROF002")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        professorRepository.save(professor);

        // When & Then
        assertThat(professorRepository.existsByEmail("professor@test.com")).isTrue();
        assertThat(professorRepository.existsByEmail("other@test.com")).isFalse();
    }

    @Test
    void shouldFindAllProfessors() {
        // Given
        Professor professor1 = new Professor();
        professor1.setNombre("Professor 1");
        professor1.setCodigo("PROF001");
        professor1.setEmail("professor1@test.com");
        professor1.setEspecialidad("Matemáticas");
        professor1.setPassword("password123");

        Professor professor2 = new Professor();
        professor2.setNombre("Professor 2");
        professor2.setCodigo("PROF002");
        professor2.setEmail("professor2@test.com");
        professor2.setEspecialidad("Física");
        professor2.setPassword("password123");

        professorRepository.save(professor1);
        professorRepository.save(professor2);

        // When
        var professors = professorRepository.findAll();

        // Then
        assertThat(professors).hasSizeGreaterThanOrEqualTo(2);
    }
}