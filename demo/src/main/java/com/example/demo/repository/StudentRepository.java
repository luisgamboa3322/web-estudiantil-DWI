package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByEmail(String email);
    Optional<Student> findByEmail(String email);
}