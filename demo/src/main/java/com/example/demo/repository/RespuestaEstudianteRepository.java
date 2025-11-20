package com.example.demo.repository;

import com.example.demo.model.RespuestaEstudiante;
import com.example.demo.model.IntentoEvaluacion;
import com.example.demo.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaEstudianteRepository extends JpaRepository<RespuestaEstudiante, Long> {
    List<RespuestaEstudiante> findByIntento(IntentoEvaluacion intento);
    List<RespuestaEstudiante> findByIntentoId(Long intentoId);
    List<RespuestaEstudiante> findByPregunta(Pregunta pregunta);
    List<RespuestaEstudiante> findByPreguntaId(Long preguntaId);
    Optional<RespuestaEstudiante> findByIntentoIdAndPreguntaId(Long intentoId, Long preguntaId);
}




