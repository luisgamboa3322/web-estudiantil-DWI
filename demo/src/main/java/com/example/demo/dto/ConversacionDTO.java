package com.example.demo.dto;

import com.example.demo.model.TipoRemitente;
import java.time.LocalDateTime;

public class ConversacionDTO {
    private String conversacionId;
    private Long contactoId;
    private String contactoNombre;
    private TipoRemitente contactoTipo;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private Long mensajesNoLeidos;
    private String avatar;

    // Constructores
    public ConversacionDTO() {
        this.mensajesNoLeidos = 0L;
    }

    public ConversacionDTO(String conversacionId, Long contactoId, String contactoNombre, TipoRemitente contactoTipo) {
        this.conversacionId = conversacionId;
        this.contactoId = contactoId;
        this.contactoNombre = contactoNombre;
        this.contactoTipo = contactoTipo;
        this.mensajesNoLeidos = 0L;
    }

    // Getters y Setters
    public String getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(String conversacionId) {
        this.conversacionId = conversacionId;
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }

    public String getContactoNombre() {
        return contactoNombre;
    }

    public void setContactoNombre(String contactoNombre) {
        this.contactoNombre = contactoNombre;
    }

    public TipoRemitente getContactoTipo() {
        return contactoTipo;
    }

    public void setContactoTipo(TipoRemitente contactoTipo) {
        this.contactoTipo = contactoTipo;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public LocalDateTime getFechaUltimoMensaje() {
        return fechaUltimoMensaje;
    }

    public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) {
        this.fechaUltimoMensaje = fechaUltimoMensaje;
    }

    public Long getMensajesNoLeidos() {
        return mensajesNoLeidos;
    }

    public void setMensajesNoLeidos(Long mensajesNoLeidos) {
        this.mensajesNoLeidos = mensajesNoLeidos;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
