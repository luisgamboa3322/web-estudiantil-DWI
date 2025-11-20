package com.example.demo.dto;

import com.example.demo.model.EstadoIntento;
import java.time.LocalDateTime;

/**
 * DTO liviano para exponer la información relevante de un intento de evaluación
 * en las vistas de estudiantes y docentes sin arrastrar toda la entidad JPA
 * (que incluye relaciones perezosas que suelen romper la serialización en Thymeleaf).
 */
public record IntentoEvaluacionResumenDTO(
    Long intentoId,
    Long evaluacionId,
    Long estudianteId,
    String estudianteNombre,
    String estudianteEmail,
    Integer numeroIntento,
    EstadoIntento estado,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    Double calificacion,
    Boolean calificacionAutomatica,
    String comentarios
) {}


