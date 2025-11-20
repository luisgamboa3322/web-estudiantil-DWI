package com.example.demo.repository;

import com.example.demo.model.OpcionRespuesta;
import com.example.demo.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpcionRespuestaRepository extends JpaRepository<OpcionRespuesta, Long> {
    List<OpcionRespuesta> findByPreguntaOrderByOrden(Pregunta pregunta);
    List<OpcionRespuesta> findByPreguntaIdOrderByOrden(Long preguntaId);
    List<OpcionRespuesta> findByEsCorrectaTrue();
    List<OpcionRespuesta> findByPreguntaAndEsCorrectaTrue(Pregunta pregunta);
}




