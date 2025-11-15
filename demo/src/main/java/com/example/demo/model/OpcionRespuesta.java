package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "opciones_respuesta")
public class OpcionRespuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El texto de la opción es obligatorio")
    @Column(columnDefinition = "TEXT")
    private String texto;

    @NotNull(message = "Indicar si es correcta es obligatorio")
    private Boolean esCorrecta = false;

    private Integer orden; // Orden de la opción (útil para ordenar y completar)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    @JsonIgnoreProperties({"opciones", "respuestas", "evaluacion"})
    private Pregunta pregunta;

    // Constructor vacío
    public OpcionRespuesta() {}

    // Constructor con parámetros
    public OpcionRespuesta(String texto, Boolean esCorrecta, Integer orden, Pregunta pregunta) {
        this.texto = texto;
        this.esCorrecta = esCorrecta;
        this.orden = orden;
        this.pregunta = pregunta;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Boolean getEsCorrecta() { return esCorrecta; }
    public void setEsCorrecta(Boolean esCorrecta) { this.esCorrecta = esCorrecta; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    @JsonIgnore
    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }
}

