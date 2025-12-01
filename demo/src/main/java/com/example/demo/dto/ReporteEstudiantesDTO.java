package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para el reporte general de estudiantes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstudiantesDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String email;
    private String estado;
    private LocalDateTime fechaRegistro;
    private List<CursoAsignadoDTO> cursosInscritos;
    private int totalCursosActivos;
    private int totalCursosCompletados;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoAsignadoDTO {
        private String nombreCurso;
        private String codigoCurso;
        private String nombreProfesor;
        private String estadoAsignacion;
        private LocalDateTime fechaAsignacion;
    }
}
