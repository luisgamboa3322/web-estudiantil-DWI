package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El enunciado de la pregunta es obligatorio")
    @Column(columnDefinition = "TEXT")
    private String enunciado;

    @NotNull(message = "El tipo de pregunta es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoPregunta tipo;

    @Min(value = 0, message = "Los puntos no pueden ser negativos")
    private int puntos;

    private Integer orden; // Orden de la pregunta en la evaluación

    private String retroalimentacionCorrecta; // Feedback cuando la respuesta es correcta
    private String retroalimentacionIncorrecta; // Feedback cuando la respuesta es incorrecta

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    @JsonIgnoreProperties({"preguntas", "intentos", "semana", "profesor"})
    private Evaluacion evaluacion;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"pregunta"})
    private List<OpcionRespuesta> opciones = new ArrayList<>();

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"pregunta", "intento"})
    private List<RespuestaEstudiante> respuestas = new ArrayList<>();

    // Constructor vacío
    public Pregunta() {}

    // Constructor con parámetros
    public Pregunta(String enunciado, TipoPregunta tipo, int puntos, Evaluacion evaluacion) {
        this.enunciado = enunciado;
        this.tipo = tipo;
        this.puntos = puntos;
        this.evaluacion = evaluacion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public TipoPregunta getTipo() { return tipo; }
    public void setTipo(TipoPregunta tipo) { this.tipo = tipo; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public String getRetroalimentacionCorrecta() { return retroalimentacionCorrecta; }
    public void setRetroalimentacionCorrecta(String retroalimentacionCorrecta) { this.retroalimentacionCorrecta = retroalimentacionCorrecta; }

    public String getRetroalimentacionIncorrecta() { return retroalimentacionIncorrecta; }
    public void setRetroalimentacionIncorrecta(String retroalimentacionIncorrecta) { this.retroalimentacionIncorrecta = retroalimentacionIncorrecta; }

    @JsonIgnore
    public Evaluacion getEvaluacion() { return evaluacion; }
    public void setEvaluacion(Evaluacion evaluacion) { this.evaluacion = evaluacion; }

    public List<OpcionRespuesta> getOpciones() { return opciones; }
    public void setOpciones(List<OpcionRespuesta> opciones) { this.opciones = opciones; }

    public List<RespuestaEstudiante> getRespuestas() { return respuestas; }
    public void setRespuestas(List<RespuestaEstudiante> respuestas) { this.respuestas = respuestas; }
}

