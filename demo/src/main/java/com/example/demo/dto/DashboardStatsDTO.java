package com.example.demo.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {

    // Métricas clave
    private Long totalEstudiantesActivos;
    private Long totalProfesores;
    private Long totalCursosActivos;
    private Long totalAsignacionesActivas;
    private Double tasaOcupacionCursos;
    private Double promedioEstudiantesPorCurso;

    // Datos para gráficos
    private Map<String, Long> estudiantesPorCurso;
    private Map<String, Long> cursosPorEstado;
    private List<InscripcionTendenciaDTO> tendenciaInscripciones;

    // Actividad reciente
    private List<ActividadRecienteDTO> actividadReciente;

    // Constructors
    public DashboardStatsDTO() {
    }

    // Getters and Setters
    public Long getTotalEstudiantesActivos() {
        return totalEstudiantesActivos;
    }

    public void setTotalEstudiantesActivos(Long totalEstudiantesActivos) {
        this.totalEstudiantesActivos = totalEstudiantesActivos;
    }

    public Long getTotalProfesores() {
        return totalProfesores;
    }

    public void setTotalProfesores(Long totalProfesores) {
        this.totalProfesores = totalProfesores;
    }

    public Long getTotalCursosActivos() {
        return totalCursosActivos;
    }

    public void setTotalCursosActivos(Long totalCursosActivos) {
        this.totalCursosActivos = totalCursosActivos;
    }

    public Long getTotalAsignacionesActivas() {
        return totalAsignacionesActivas;
    }

    public void setTotalAsignacionesActivas(Long totalAsignacionesActivas) {
        this.totalAsignacionesActivas = totalAsignacionesActivas;
    }

    public Double getTasaOcupacionCursos() {
        return tasaOcupacionCursos;
    }

    public void setTasaOcupacionCursos(Double tasaOcupacionCursos) {
        this.tasaOcupacionCursos = tasaOcupacionCursos;
    }

    public Double getPromedioEstudiantesPorCurso() {
        return promedioEstudiantesPorCurso;
    }

    public void setPromedioEstudiantesPorCurso(Double promedioEstudiantesPorCurso) {
        this.promedioEstudiantesPorCurso = promedioEstudiantesPorCurso;
    }

    public Map<String, Long> getEstudiantesPorCurso() {
        return estudiantesPorCurso;
    }

    public void setEstudiantesPorCurso(Map<String, Long> estudiantesPorCurso) {
        this.estudiantesPorCurso = estudiantesPorCurso;
    }

    public Map<String, Long> getCursosPorEstado() {
        return cursosPorEstado;
    }

    public void setCursosPorEstado(Map<String, Long> cursosPorEstado) {
        this.cursosPorEstado = cursosPorEstado;
    }

    public List<InscripcionTendenciaDTO> getTendenciaInscripciones() {
        return tendenciaInscripciones;
    }

    public void setTendenciaInscripciones(List<InscripcionTendenciaDTO> tendenciaInscripciones) {
        this.tendenciaInscripciones = tendenciaInscripciones;
    }

    public List<ActividadRecienteDTO> getActividadReciente() {
        return actividadReciente;
    }

    public void setActividadReciente(List<ActividadRecienteDTO> actividadReciente) {
        this.actividadReciente = actividadReciente;
    }
}
