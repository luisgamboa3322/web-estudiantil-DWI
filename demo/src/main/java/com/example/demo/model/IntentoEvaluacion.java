package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intentos_evaluacion")
public class IntentoEvaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El número de intento es obligatorio")
    private Integer numeroIntento; // 1, 2, 3, etc.

    @NotNull(message = "El estado del intento es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoIntento estado = EstadoIntento.EN_PROGRESO;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaFin; // null si aún está en progreso

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaLimiteIntento; // Calculado basado en tiempo límite de la evaluación

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    @JsonIgnoreProperties({"preguntas", "intentos", "semana", "profesor"})
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "cursos"})
    private Student estudiante;

    @OneToMany(mappedBy = "intento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"intento", "pregunta"})
    private List<RespuestaEstudiante> respuestas = new ArrayList<>();

    @OneToOne(mappedBy = "intento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"intento"})
    private Calificacion calificacion;

    // Constructor vacío
    public IntentoEvaluacion() {
        this.fechaInicio = LocalDateTime.now();
    }

    // Constructor con parámetros
    public IntentoEvaluacion(Integer numeroIntento, Evaluacion evaluacion, Student estudiante) {
        this.numeroIntento = numeroIntento;
        this.evaluacion = evaluacion;
        this.estudiante = estudiante;
        this.fechaInicio = LocalDateTime.now();
        this.estado = EstadoIntento.EN_PROGRESO;
        
        // Calcular fecha límite del intento si hay tiempo límite
        if (evaluacion.getTiempoLimiteMinutos() != null) {
            this.fechaLimiteIntento = this.fechaInicio.plusMinutes(evaluacion.getTiempoLimiteMinutos());
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumeroIntento() { return numeroIntento; }
    public void setNumeroIntento(Integer numeroIntento) { this.numeroIntento = numeroIntento; }

    public EstadoIntento getEstado() { return estado; }
    public void setEstado(EstadoIntento estado) { this.estado = estado; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public LocalDateTime getFechaLimiteIntento() { return fechaLimiteIntento; }
    public void setFechaLimiteIntento(LocalDateTime fechaLimiteIntento) { this.fechaLimiteIntento = fechaLimiteIntento; }

    public Evaluacion getEvaluacion() { return evaluacion; }
    public void setEvaluacion(Evaluacion evaluacion) { this.evaluacion = evaluacion; }

    public Student getEstudiante() { return estudiante; }
    public void setEstudiante(Student estudiante) { this.estudiante = estudiante; }

    public List<RespuestaEstudiante> getRespuestas() { return respuestas; }
    public void setRespuestas(List<RespuestaEstudiante> respuestas) { this.respuestas = respuestas; }

    public Calificacion getCalificacion() { return calificacion; }
    public void setCalificacion(Calificacion calificacion) { this.calificacion = calificacion; }

    // Métodos útiles
    public boolean isExpirado() {
        if (fechaLimiteIntento == null) return false;
        return LocalDateTime.now().isAfter(fechaLimiteIntento) && estado == EstadoIntento.EN_PROGRESO;
    }

    public boolean isCompletado() {
        return estado == EstadoIntento.COMPLETADO || estado == EstadoIntento.CALIFICADO;
    }

    public long getTiempoRestanteSegundos() {
        if (fechaLimiteIntento == null) return -1; // Sin límite
        if (isExpirado() || isCompletado()) return 0;
        long segundos = java.time.Duration.between(LocalDateTime.now(), fechaLimiteIntento).getSeconds();
        return Math.max(0, segundos);
    }
}

