package com.example.demo.dto;

import com.example.demo.model.TipoRemitente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MensajeDTO {
    private String conversacionId;

    @NotNull(message = "El ID del remitente es obligatorio")
    private Long remitenteId;

    @NotNull(message = "El tipo de remitente es obligatorio")
    private TipoRemitente tipoRemitente;

    private String remitenteNombre;

    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long destinatarioId;

    @NotNull(message = "El tipo de destinatario es obligatorio")
    private TipoRemitente tipoDestinatario;

    private String destinatarioNombre;

    @NotBlank(message = "El contenido del mensaje no puede estar vacío")
    private String contenido;

    private LocalDateTime fechaEnvio;

    private Boolean leido;

    // Constructores
    public MensajeDTO() {
        this.fechaEnvio = LocalDateTime.now();
        this.leido = false;
    }

    // Getters y Setters
    public String getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(String conversacionId) {
        this.conversacionId = conversacionId;
    }

    public Long getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(Long remitenteId) {
        this.remitenteId = remitenteId;
    }

    public TipoRemitente getTipoRemitente() {
        return tipoRemitente;
    }

    public void setTipoRemitente(TipoRemitente tipoRemitente) {
        this.tipoRemitente = tipoRemitente;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public TipoRemitente getTipoDestinatario() {
        return tipoDestinatario;
    }

    public void setTipoDestinatario(TipoRemitente tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public void setDestinatarioNombre(String destinatarioNombre) {
        this.destinatarioNombre = destinatarioNombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }
}
