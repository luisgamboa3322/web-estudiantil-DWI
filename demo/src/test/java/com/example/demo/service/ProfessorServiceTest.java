package com.example.demo.service;

import com.example.demo.model.Professor;
import com.example.demo.repository.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProfessorServiceTest {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateProfessorSuccessfully() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");

        // When
        Professor createdProfessor = professorService.create(professor);

        // Then
        assertThat(createdProfessor.getId()).isNotNull();
        assertThat(createdProfessor.getNombre()).isEqualTo("Professor Test");
        assertThat(createdProfessor.getCodigo()).isEqualTo("PROF001");
        assertThat(createdProfessor.getEmail()).isEqualTo("professor@test.com");
        assertThat(createdProfessor.getEspecialidad()).isEqualTo("Matemáticas");
        assertThat(passwordEncoder.matches("password123", createdProfessor.getPassword())).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCreatingProfessorWithExistingCodigo() {
        // Given
        Professor professor1 = new Professor();
        professor1.setNombre("Professor 1");
        professor1.setCodigo("PROF001");
        professor1.setEmail("professor1@test.com");
        professor1.setEspecialidad("Matemáticas");
        professor1.setPassword("password123");
        professorService.create(professor1);

        Professor professor2 = new Professor();
        professor2.setNombre("Professor 2");
        professor2.setCodigo("PROF001"); // Same codigo
        professor2.setEmail("professor2@test.com");
        professor2.setEspecialidad("Física");
        professor2.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> professorService.create(professor2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Código ya existe");
    }

    @Test
    void shouldThrowExceptionWhenCreatingProfessorWithExistingEmail() {
        // Given
        Professor professor1 = new Professor();
        professor1.setNombre("Professor 1");
        professor1.setCodigo("PROF001");
        professor1.setEmail("professor@test.com");
        professor1.setEspecialidad("Matemáticas");
        professor1.setPassword("password123");
        professorService.create(professor1);

        Professor professor2 = new Professor();
        professor2.setNombre("Professor 2");
        professor2.setCodigo("PROF002");
        professor2.setEmail("professor@test.com"); // Same email
        professor2.setEspecialidad("Física");
        professor2.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> professorService.create(professor2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email ya existe");
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
        professorService.create(professor);

        // When
        Optional<Professor> foundProfessor = professorService.findByEmail("professor@test.com");

        // Then
        assertThat(foundProfessor).isPresent();
        assertThat(foundProfessor.get().getEmail()).isEqualTo("professor@test.com");
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
        professorService.create(professor1);

        Professor professor2 = new Professor();
        professor2.setNombre("Professor 2");
        professor2.setCodigo("PROF002");
        professor2.setEmail("professor2@test.com");
        professor2.setEspecialidad("Física");
        professor2.setPassword("password123");
        professorService.create(professor2);

        // When
        List<Professor> professors = professorService.findAll();

        // Then
        assertThat(professors).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldUpdateProfessorSuccessfully() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Original");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor createdProfessor = professorService.create(professor);

        Professor changes = new Professor();
        changes.setNombre("Professor Updated");
        changes.setEmail("professor.updated@test.com");
        changes.setEspecialidad("Física Cuántica");

        // When
        Professor updatedProfessor = professorService.update(createdProfessor.getId(), changes);

        // Then
        assertThat(updatedProfessor.getNombre()).isEqualTo("Professor Updated");
        assertThat(updatedProfessor.getEmail()).isEqualTo("professor.updated@test.com");
        assertThat(updatedProfessor.getEspecialidad()).isEqualTo("Física Cuántica");
        assertThat(updatedProfessor.getCodigo()).isEqualTo("PROF001"); // Unchanged
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        // Given
        Professor professor1 = new Professor();
        professor1.setNombre("Professor 1");
        professor1.setCodigo("PROF001");
        professor1.setEmail("professor1@test.com");
        professor1.setEspecialidad("Matemáticas");
        professor1.setPassword("password123");
        Professor createdProfessor1 = professorService.create(professor1);

        Professor professor2 = new Professor();
        professor2.setNombre("Professor 2");
        professor2.setCodigo("PROF002");
        professor2.setEmail("professor2@test.com");
        professor2.setEspecialidad("Física");
        professor2.setPassword("password123");
        Professor createdProfessor2 = professorService.create(professor2);

        Professor changes = new Professor();
        changes.setEmail("professor2@test.com"); // Existing email

        // When & Then
        assertThatThrownBy(() -> professorService.update(createdProfessor1.getId(), changes))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email ya existe");
    }

    @Test
    void shouldDeleteProfessorSuccessfully() {
        // Given
        Professor professor = new Professor();
        professor.setNombre("Professor Test");
        professor.setCodigo("PROF001");
        professor.setEmail("professor@test.com");
        professor.setEspecialidad("Matemáticas");
        professor.setPassword("password123");
        Professor createdProfessor = professorService.create(professor);

        // When
        professorService.delete(createdProfessor.getId());

        // Then
        assertThat(professorService.getById(createdProfessor.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProfessor() {
        // When & Then
        assertThatThrownBy(() -> professorService.delete(999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Profesor no encontrado");
    }
}
