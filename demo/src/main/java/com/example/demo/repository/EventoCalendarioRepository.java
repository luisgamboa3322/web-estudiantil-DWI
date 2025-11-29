package com.example.demo.repository;

import com.example.demo.model.EventoCalendario;
import com.example.demo.model.Student;
import com.example.demo.model.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoCalendarioRepository extends JpaRepository<EventoCalendario, Long> {

    // Encontrar todos los eventos de un estudiante
    List<EventoCalendario> findByEstudianteOrderByFechaInicioAsc(Student estudiante);

    // Encontrar eventos de un estudiante en un rango de fechas
    @Query("SELECT e FROM EventoCalendario e WHERE e.estudiante = :estudiante " +
            "AND e.fechaInicio >= :fechaInicio AND e.fechaInicio <= :fechaFin " +
            "ORDER BY e.fechaInicio ASC")
    List<EventoCalendario> findByEstudianteAndFechaRange(
            @Param("estudiante") Student estudiante,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // Encontrar eventos por tipo
    List<EventoCalendario> findByEstudianteAndTipoOrderByFechaInicioAsc(Student estudiante, TipoEvento tipo);

    // Encontrar eventos próximos (para notificaciones)
    @Query("SELECT e FROM EventoCalendario e WHERE e.estudiante = :estudiante " +
            "AND e.fechaInicio > :ahora AND e.fechaInicio <= :limite " +
            "AND e.notificacionEnviada = false " +
            "ORDER BY e.fechaInicio ASC")
    List<EventoCalendario> findProximosEventosSinNotificar(
            @Param("estudiante") Student estudiante,
            @Param("ahora") LocalDateTime ahora,
            @Param("limite") LocalDateTime limite);
}
