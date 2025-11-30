package com.example.demo.repository;

import com.example.demo.model.Tarea;
import com.example.demo.model.Semana;
import com.example.demo.model.Professor;
import com.example.demo.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findBySemanaOrderByFechaCreacion(Semana semana);

    List<Tarea> findBySemanaIdOrderByFechaCreacion(Long semanaId);

    List<Tarea> findByProfesor(Professor profesor);

    List<Tarea> findByFechaLimiteBefore(LocalDateTime fecha);

    List<Tarea> findByFechaLimiteAfter(LocalDateTime fecha);

    List<Tarea> findBySemana_CursoAndFechaLimiteBetween(Curso curso, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Tarea> findBySemana_Curso(Curso curso);
}