package com.example.demo.dto;

import com.example.demo.model.TipoRemitente;

public class ContactoDTO {
    private Long id;
    private String nombre;
    private TipoRemitente tipo;
    private String email;
    private String avatar;
    private Boolean enLinea;
    private String ultimoMensaje;
    private Long mensajesNoLeidos;

    // Constructores
    public ContactoDTO() {
        this.enLinea = false;
        this.mensajesNoLeidos = 0L;
    }

    public ContactoDTO(Long id, String nombre, TipoRemitente tipo, String email) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.email = email;
        this.enLinea = false;
        this.mensajesNoLeidos = 0L;
    }

    // Getters y Setters
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

    public TipoRemitente getTipo() {
        return tipo;
    }

    public void setTipo(TipoRemitente tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getEnLinea() {
        return enLinea;
    }

    public void setEnLinea(Boolean enLinea) {
        this.enLinea = enLinea;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public Long getMensajesNoLeidos() {
        return mensajesNoLeidos;
    }

    public void setMensajesNoLeidos(Long mensajesNoLeidos) {
        this.mensajesNoLeidos = mensajesNoLeidos;
    }
}
