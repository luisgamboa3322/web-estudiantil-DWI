package com.example.demo.repository;

import com.example.demo.model.Semana;
import com.example.demo.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemanaRepository extends JpaRepository<Semana, Long> {
    List<Semana> findByCursoOrderByNumeroSemana(Curso curso);
    List<Semana> findByCursoIdOrderByNumeroSemana(Long cursoId);
    boolean existsByCursoAndNumeroSemana(Curso curso, int numeroSemana);
}