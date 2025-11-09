package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas_tarea")
public class EntregaTarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido; // Texto o URL del archivo entregado

    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDateTime fechaEntrega;

    private int calificacion; // -1 = no calificada, 0-100 = calificación

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id", nullable = false)
    @JsonIgnoreProperties({"semana", "profesor", "entregas"})
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "cursos"})
    private Student student;

    // Constructor vacío
    public EntregaTarea() {}

    // Constructor con parámetros
    public EntregaTarea(String contenido, Tarea tarea, Student student) {
        this.contenido = contenido;
        this.tarea = tarea;
        this.student = student;
        this.fechaEntrega = LocalDateTime.now();
        this.calificacion = -1; // No calificada por defecto
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public Tarea getTarea() { return tarea; }
    public void setTarea(Tarea tarea) { this.tarea = tarea; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    // Métodos útiles
    public boolean isCalificada() {
        return calificacion >= 0;
    }

    public boolean isEntregadaATiempo() {
        return fechaEntrega.isBefore(tarea.getFechaLimite()) || fechaEntrega.isEqual(tarea.getFechaLimite());
    }
}