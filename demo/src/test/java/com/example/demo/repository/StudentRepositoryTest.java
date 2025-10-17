package com.example.demo.repository;

import com.example.demo.model.Student;
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
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void shouldSaveAndFindStudent() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");

        // When
        Student savedStudent = studentRepository.save(student);

        // Then
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getNombre()).isEqualTo("Student Test");
        assertThat(savedStudent.getCodigo()).isEqualTo("STU001");
        assertThat(savedStudent.getEmail()).isEqualTo("student@test.com");
    }

    @Test
    void shouldFindStudentByEmail() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        studentRepository.save(student);

        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("student@test.com");

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getEmail()).isEqualTo("student@test.com");
    }

    @Test
    void shouldCheckExistsByCodigo() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        studentRepository.save(student);

        // When & Then
        assertThat(studentRepository.existsByCodigo("STU001")).isTrue();
        assertThat(studentRepository.existsByCodigo("STU002")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        // Given
        Student student = new Student();
        student.setNombre("Student Test");
        student.setCodigo("STU001");
        student.setEmail("student@test.com");
        student.setPassword("password123");
        studentRepository.save(student);

        // When & Then
        assertThat(studentRepository.existsByEmail("student@test.com")).isTrue();
        assertThat(studentRepository.existsByEmail("other@test.com")).isFalse();
    }

    @Test
    void shouldFindAllStudents() {
        // Given
        Student student1 = new Student();
        student1.setNombre("Student 1");
        student1.setCodigo("STU001");
        student1.setEmail("student1@test.com");
        student1.setPassword("password123");

        Student student2 = new Student();
        student2.setNombre("Student 2");
        student2.setCodigo("STU002");
        student2.setEmail("student2@test.com");
        student2.setPassword("password123");

        studentRepository.save(student1);
        studentRepository.save(student2);

        // When
        var students = studentRepository.findAll();

        // Then
        assertThat(students).hasSizeGreaterThanOrEqualTo(2);
    }
}