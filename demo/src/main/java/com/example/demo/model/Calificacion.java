package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones")
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La calificación es obligatoria")
    private Double calificacion; // Calificación numérica (0-100 o según puntos máximos)

    private String comentarios; // Comentarios del profesor

    @NotNull(message = "La fecha de calificación es obligatoria")
    private LocalDateTime fechaCalificacion;

    private Boolean calificacionAutomatica = false; // true si fue calificada automáticamente

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intento_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"calificacion", "respuestas", "evaluacion", "estudiante"})
    private IntentoEvaluacion intento;

    // Constructor vacío
    public Calificacion() {
        this.fechaCalificacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Calificacion(Double calificacion, IntentoEvaluacion intento, Boolean calificacionAutomatica) {
        this.calificacion = calificacion;
        this.intento = intento;
        this.calificacionAutomatica = calificacionAutomatica;
        this.fechaCalificacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public LocalDateTime getFechaCalificacion() { return fechaCalificacion; }
    public void setFechaCalificacion(LocalDateTime fechaCalificacion) { this.fechaCalificacion = fechaCalificacion; }

    public Boolean getCalificacionAutomatica() { return calificacionAutomatica; }
    public void setCalificacionAutomatica(Boolean calificacionAutomatica) { this.calificacionAutomatica = calificacionAutomatica; }

    public IntentoEvaluacion getIntento() { return intento; }
    public void setIntento(IntentoEvaluacion intento) { this.intento = intento; }
}




