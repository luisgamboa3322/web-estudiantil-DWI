package com.example.demo.repository;

import com.example.demo.model.IntentoEvaluacion;
import com.example.demo.model.Evaluacion;
import com.example.demo.model.Student;
import com.example.demo.model.EstadoIntento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntentoEvaluacionRepository extends JpaRepository<IntentoEvaluacion, Long> {
    List<IntentoEvaluacion> findByEvaluacion(Evaluacion evaluacion);
    List<IntentoEvaluacion> findByEvaluacionId(Long evaluacionId);
    List<IntentoEvaluacion> findByEstudiante(Student estudiante);
    List<IntentoEvaluacion> findByEstudianteId(Long estudianteId);
    List<IntentoEvaluacion> findByEvaluacionAndEstudiante(Evaluacion evaluacion, Student estudiante);
    List<IntentoEvaluacion> findByEvaluacionIdAndEstudianteId(Long evaluacionId, Long estudianteId);
    Optional<IntentoEvaluacion> findByEvaluacionIdAndEstudianteIdAndNumeroIntento(Long evaluacionId, Long estudianteId, Integer numeroIntento);
    List<IntentoEvaluacion> findByEstado(EstadoIntento estado);
    Integer countByEvaluacionIdAndEstudianteId(Long evaluacionId, Long estudianteId);
}




