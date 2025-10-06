package com.example.demo.repository;

import com.example.demo.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByEmail(String email);
    Optional<Professor> findByCodigo(String codigo);
    Optional<Professor> findByEmail(String email);
}
