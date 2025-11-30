package com.example.demo.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class NotificacionDTO {
    private Long id;
    private String titulo;
    private String mensaje;
    private String fechaHace; // "Hace 2 horas", etc.
    private boolean leida;
    private String tipo;
    private String link;

    public NotificacionDTO(Long id, String titulo, String mensaje, LocalDateTime fechaCreacion, boolean leida,
            String tipo, String link) {
        this.id = id;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = leida;
        this.tipo = tipo;
        this.link = link;
        this.fechaHace = calcularFechaHace(fechaCreacion);
    }

    private String calcularFechaHace(LocalDateTime fecha) {
        LocalDateTime now = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(fecha, now);

        if (minutos < 1)
            return "Hace un momento";
        if (minutos < 60)
            return "Hace " + minutos + " minutos";

        long horas = ChronoUnit.HOURS.between(fecha, now);
        if (horas < 24)
            return "Hace " + horas + " horas";

        long dias = ChronoUnit.DAYS.between(fecha, now);
        if (dias == 1)
            return "Ayer";
        if (dias < 7)
            return "Hace " + dias + " días";

        return fecha.toLocalDate().toString(); // O un formato más bonito
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFechaHace() {
        return fechaHace;
    }

    public boolean isLeida() {
        return leida;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLink() {
        return link;
    }
}
