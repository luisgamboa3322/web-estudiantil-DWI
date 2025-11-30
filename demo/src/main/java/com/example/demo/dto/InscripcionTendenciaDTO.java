package com.example.demo.dto;

public class InscripcionTendenciaDTO {
    private String fecha;
    private Long cantidad;

    public InscripcionTendenciaDTO() {
    }

    public InscripcionTendenciaDTO(String fecha, Long cantidad) {
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
