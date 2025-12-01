package com.example.demo.repository;

import com.example.demo.model.Curso;
import com.example.demo.model.EstadoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import com.example.demo.model.Professor;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    @Query("SELECT c FROM Curso c WHERE c.profesor.id = :profesorId")
    List<Curso> findByProfesorId(@Param("profesorId") Long profesorId);

    Optional<Curso> findByCodigo(String codigo);

    List<Curso> findByEstado(String estado);

    long countByEstado(EstadoCurso estado);

    List<Curso> findByProfesor(Professor profesor);
}