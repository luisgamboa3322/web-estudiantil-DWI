package com.example.demo.dto;

import java.time.LocalDateTime;

public class ActividadRecienteDTO {
    private String tipo; // "ESTUDIANTE", "PROFESOR", "CURSO", "ASIGNACION"
    private String descripcion;
    private LocalDateTime fecha;
    private String icono;

    public ActividadRecienteDTO() {
    }

    public ActividadRecienteDTO(String tipo, String descripcion, LocalDateTime fecha, String icono) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.icono = icono;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
}
