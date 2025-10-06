package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "curso")
public class Curso {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String codigo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoCurso estado = EstadoCurso.ACTIVO;

    // Relación con profesor (puede ser opcional inicialmente)
    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Professor profesor;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoCurso getEstado() { return estado; }
    public void setEstado(EstadoCurso estado) { this.estado = estado; }

    public Professor getProfesor() { return profesor; }
    public void setProfesor(Professor profesor) { this.profesor = profesor; }
}
