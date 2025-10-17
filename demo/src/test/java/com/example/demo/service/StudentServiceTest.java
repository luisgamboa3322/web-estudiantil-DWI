package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
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
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldSaveStudentSuccessfully() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");

        // When
        Student savedStudent = studentService.save(student);

        // Then
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getNombre()).isEqualTo("Student Test");
        assertThat(savedStudent.getCodigo()).isEqualTo("STU001");
        assertThat(savedStudent.getEmail()).isEqualTo("student@test.com");
        assertThat(passwordEncoder.matches("password123", savedStudent.getPassword())).isTrue();
    }

    @Test
    void shouldFindStudentByEmail() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        studentService.save(student);

        // When
        Optional<Student> foundStudent = studentService.findByEmail("student@test.com");

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getEmail()).isEqualTo("student@test.com");
    }

    @Test
    void shouldFindAllStudents() {
        // Given
        Student student1 = new Student();
        student1.setNombre("Student 1");
        student1.setCodigo("STU001");
        student1.setEmail("student1@test.com");
        student1.setPassword("password123");
        studentService.save(student1);

        Student student2 = new Student();
        student2.setNombre("Student 2");
        student2.setCodigo("STU002");
        student2.setEmail("student2@test.com");
        student2.setPassword("password123");
        studentService.save(student2);

        // When
        List<Student> students = studentService.findAll();

        // Then
        assertThat(students).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldUpdateStudentSuccessfully() {
        // Given
        Student student = new Student();
        student.setNombre("Student Original");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentService.save(student);

        Student changes = new Student();
        changes.setNombre("Student Updated");
        changes.setEmail("student.updated@test.com");

        // When
        Student updatedStudent = studentService.update(savedStudent.getId(), changes);

        // Then
        assertThat(updatedStudent.getNombre()).isEqualTo("Student Updated");
        assertThat(updatedStudent.getEmail()).isEqualTo("student.updated@test.com");
        assertThat(updatedStudent.getCodigo()).isEqualTo("STU001"); // Unchanged
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        // Given
        Student student1 = new Student();
        student1.setNombre("Student 1");
        student1.setCodigo("STU001");
        student1.setEmail("student1@test.com");
        student1.setPassword("password123");
        Student savedStudent1 = studentService.save(student1);

        Student student2 = new Student();
        student2.setNombre("Student 2");
        student2.setCodigo("STU002");
        student2.setEmail("student2@test.com");
        student2.setPassword("password123");
        Student savedStudent2 = studentService.save(student2);

        Student changes = new Student();
        changes.setEmail("student2@test.com"); // Existing email

        // When & Then
        assertThatThrownBy(() -> studentService.update(savedStudent1.getId(), changes))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email ya existe");
    }

    @Test
    void shouldDeleteStudentSuccessfully() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        Student savedStudent = studentService.save(student);

        // When
        studentService.delete(savedStudent.getId());

        // Then
        assertThat(studentService.findById(savedStudent.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentStudent() {
        // When & Then
        assertThatThrownBy(() -> studentService.delete(999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Estudiante no encontrado");
    }
}