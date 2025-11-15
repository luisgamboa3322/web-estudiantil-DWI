package com.example.demo.repository;

import com.example.demo.model.Pregunta;
import com.example.demo.model.Evaluacion;
import com.example.demo.model.TipoPregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByEvaluacionOrderByOrden(Evaluacion evaluacion);
    List<Pregunta> findByEvaluacionIdOrderByOrden(Long evaluacionId);
    List<Pregunta> findByTipo(TipoPregunta tipo);
}

