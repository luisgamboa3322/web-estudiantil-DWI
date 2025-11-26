package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversacion_id", nullable = false)
    private String conversacionId;

    @Column(name = "remitente_id", nullable = false)
    private Long remitenteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_remitente", nullable = false)
    private TipoRemitente tipoRemitente;

    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_destinatario", nullable = false)
    private TipoRemitente tipoDestinatario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean leido = false;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
