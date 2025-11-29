package com.example.demo.dto;

import com.example.demo.model.TipoEvento;
import java.time.LocalDateTime;

public class EventoCalendarioDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private TipoEvento tipo;
    private String color;
    private Long cursoId;
    private String cursoNombre;
    private Boolean esEditable;

    public EventoCalendarioDTO() {
    }

    public EventoCalendarioDTO(Long id, String titulo, String descripcion,
            LocalDateTime fechaInicio, LocalDateTime fechaFin,
            TipoEvento tipo, String color, Long cursoId,
            String cursoNombre, Boolean esEditable) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.color = color;
        this.cursoId = cursoId;
        this.cursoNombre = cursoNombre;
        this.esEditable = esEditable;
    }

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

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public Boolean getEsEditable() {
        return esEditable;
    }

    public void setEsEditable(Boolean esEditable) {
        this.esEditable = esEditable;
    }
}
