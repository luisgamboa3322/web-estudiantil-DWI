package com.example.demo.repository;

import com.example.demo.model.Evaluacion;
import com.example.demo.model.Semana;
import com.example.demo.model.Professor;
import com.example.demo.model.EstadoEvaluacion;
import com.example.demo.model.TipoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findBySemanaOrderByFechaCreacion(Semana semana);
    List<Evaluacion> findBySemanaIdOrderByFechaCreacion(Long semanaId);
    List<Evaluacion> findByProfesor(Professor profesor);
    List<Evaluacion> findByEstado(EstadoEvaluacion estado);
    List<Evaluacion> findByTipo(TipoEvaluacion tipo);
    List<Evaluacion> findByFechaInicioBeforeAndFechaLimiteAfter(LocalDateTime fecha1, LocalDateTime fecha2);
    List<Evaluacion> findByFechaLimiteBefore(LocalDateTime fecha);
    List<Evaluacion> findByFechaLimiteAfter(LocalDateTime fecha);
}




