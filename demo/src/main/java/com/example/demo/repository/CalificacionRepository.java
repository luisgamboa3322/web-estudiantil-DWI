package com.example.demo.repository;

import com.example.demo.model.Calificacion;
import com.example.demo.model.IntentoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    Optional<Calificacion> findByIntento(IntentoEvaluacion intento);
    Optional<Calificacion> findByIntentoId(Long intentoId);
}

