package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título de la tarea es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha límite es obligatoria")
    private LocalDateTime fechaLimite;

    @Min(value = 0, message = "Los puntos máximos no pueden ser negativos")
    private int puntosMaximos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semana_id", nullable = false)
    @JsonIgnoreProperties({"curso", "materiales", "tareas"})
    private Semana semana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "cursos"})
    private Professor profesor;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Tarea() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Tarea(String titulo, String descripcion, LocalDateTime fechaLimite, int puntosMaximos, Semana semana, Professor profesor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.puntosMaximos = puntosMaximos;
        this.semana = semana;
        this.profesor = profesor;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }

    public int getPuntosMaximos() { return puntosMaximos; }
    public void setPuntosMaximos(int puntosMaximos) { this.puntosMaximos = puntosMaximos; }

    public Semana getSemana() { return semana; }
    public void setSemana(Semana semana) { this.semana = semana; }

    public Professor getProfesor() { return profesor; }
    public void setProfesor(Professor profesor) { this.profesor = profesor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}