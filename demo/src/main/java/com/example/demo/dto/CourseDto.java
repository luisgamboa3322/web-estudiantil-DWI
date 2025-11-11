package com.example.demo.dto;

import com.example.demo.model.EstadoCurso;
import java.time.LocalDateTime;

public class CourseDto {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private EstadoCurso estado;
    private ProfessorDto profesor;
    private LocalDateTime fechaCreacion;

    public CourseDto() {}

    public CourseDto(Long id, String nombre, String codigo, String descripcion, 
                    EstadoCurso estado, ProfessorDto profesor) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.profesor = profesor;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoCurso getEstado() {
        return estado;
    }

    public void setEstado(EstadoCurso estado) {
        this.estado = estado;
    }

    public ProfessorDto getProfesor() {
        return profesor;
    }

    public void setProfesor(ProfessorDto profesor) {
        this.profesor = profesor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}