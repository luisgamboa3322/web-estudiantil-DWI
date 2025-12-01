package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para el reporte de asignaciones (inscripciones)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAsignacionesDTO {
    private Long id;
    private String nombreEstudiante;
    private String codigoEstudiante;
    private String nombreCurso;
    private String codigoCurso;
    private String nombreProfesor;
    private String estadoAsignacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaCompletado;
    private int duracionDias;
}
