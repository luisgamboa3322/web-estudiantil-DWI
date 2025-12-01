package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el reporte de ocupación de cursos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCursosDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private String estado;
    private String nombreProfesor;
    private String especialidadProfesor;
    private int numeroEstudiantesInscritos;
    private int capacidadMaxima;
    private double tasaOcupacion; // Porcentaje de ocupación
    private String nivelDemanda; // "ALTA", "MEDIA", "BAJA"
}
