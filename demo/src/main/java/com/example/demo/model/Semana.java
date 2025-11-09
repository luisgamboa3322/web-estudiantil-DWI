package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "semanas")
public class Semana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El número de semana es obligatorio")
    @Min(value = 1, message = "El número de semana debe ser mayor a 0")
    private int numeroSemana;

    @NotBlank(message = "El título de la semana es obligatorio")
    private String titulo;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    // Constructor vacío
    public Semana() {}

    // Constructor con parámetros
    public Semana(int numeroSemana, String titulo, String descripcion, Curso curso) {
        this.numeroSemana = numeroSemana;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.curso = curso;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumeroSemana() { return numeroSemana; }
    public void setNumeroSemana(int numeroSemana) { this.numeroSemana = numeroSemana; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
}