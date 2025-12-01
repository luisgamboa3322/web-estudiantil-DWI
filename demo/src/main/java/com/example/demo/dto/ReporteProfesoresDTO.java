package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para el reporte de carga académica de profesores
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteProfesoresDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String email;
    private String especialidad;
    private int numeroCursosAsignados;
    private int numeroCursosActivos;
    private int numeroCursosInactivos;
    private int totalEstudiantesBajoSuCargo;
    private List<CursoProfesorDTO> cursosAsignados;
    private String nivelCarga; // "LIGERA", "MEDIA", "ALTA"

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoProfesorDTO {
        private String nombreCurso;
        private String codigoCurso;
        private String estadoCurso;
        private int numeroEstudiantes;
    }
}
