package com.example.demo.dto;

import com.example.demo.model.EstadoAsignacion;
import java.util.List;

/**
 * Representa el resumen de participación de un estudiante en una evaluación
 * (incluye la información del estudiante y sus intentos, si los hubiera).
 */
public record ResultadoEvaluacionAlumnoDTO(
    Long estudianteId,
    String estudianteNombre,
    String estudianteEmail,
    EstadoAsignacion estadoAsignacion,
    int totalIntentos,
    IntentoEvaluacionResumenDTO ultimoIntento,
    List<IntentoEvaluacionResumenDTO> intentos
) {}


