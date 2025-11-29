package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_calendario")
public class EventoCalendario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del evento es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotNull(message = "El tipo de evento es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoEvento tipo;

    @NotNull(message = "El color del evento es obligatorio")
    private String color; // Código hexadecimal del color

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({ "password", "roles", "cursos" })
    private Student estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    @JsonIgnoreProperties({ "semanas", "estudiantes", "profesor" })
    private Curso curso; // Opcional, si el evento está relacionado con un curso

    private Boolean notificacionEnviada = false;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public EventoCalendario() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public EventoCalendario(String titulo, String descripcion, LocalDateTime fechaInicio,
            LocalDateTime fechaFin, TipoEvento tipo, String color,
            Student estudiante, Curso curso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.color = color;
        this.estudiante = estudiante;
        this.curso = curso;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Student getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Student estudiante) {
        this.estudiante = estudiante;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Boolean getNotificacionEnviada() {
        return notificacionEnviada;
    }

    public void setNotificacionEnviada(Boolean notificacionEnviada) {
        this.notificacionEnviada = notificacionEnviada;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
