package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_estudiante")
public class RespuestaEstudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String respuestaTexto; // Para preguntas de desarrollo, completar, etc.

    private Long opcionSeleccionadaId; // Para opción múltiple, verdadero/falso (ID de OpcionRespuesta)

    private String opcionesOrdenadas; // Para preguntas de ordenar (JSON array de IDs en orden)

    private Boolean esCorrecta; // null = no calificada, true/false = calificada automáticamente
    private Integer puntosObtenidos; // Puntos obtenidos en esta pregunta

    private String retroalimentacion; // Feedback personalizado del profesor

    @NotNull(message = "La fecha de respuesta es obligatoria")
    private LocalDateTime fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intento_id", nullable = false)
    @JsonIgnoreProperties({"respuestas", "calificacion", "evaluacion", "estudiante"})
    private IntentoEvaluacion intento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    @JsonIgnoreProperties({"opciones", "respuestas", "evaluacion"})
    private Pregunta pregunta;

    // Constructor vacío
    public RespuestaEstudiante() {
        this.fechaRespuesta = LocalDateTime.now();
    }

    // Constructor con parámetros
    public RespuestaEstudiante(IntentoEvaluacion intento, Pregunta pregunta) {
        this.intento = intento;
        this.pregunta = pregunta;
        this.fechaRespuesta = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRespuestaTexto() { return respuestaTexto; }
    public void setRespuestaTexto(String respuestaTexto) { this.respuestaTexto = respuestaTexto; }

    public Long getOpcionSeleccionadaId() { return opcionSeleccionadaId; }
    public void setOpcionSeleccionadaId(Long opcionSeleccionadaId) { this.opcionSeleccionadaId = opcionSeleccionadaId; }

    public String getOpcionesOrdenadas() { return opcionesOrdenadas; }
    public void setOpcionesOrdenadas(String opcionesOrdenadas) { this.opcionesOrdenadas = opcionesOrdenadas; }

    public Boolean getEsCorrecta() { return esCorrecta; }
    public void setEsCorrecta(Boolean esCorrecta) { this.esCorrecta = esCorrecta; }

    public Integer getPuntosObtenidos() { return puntosObtenidos; }
    public void setPuntosObtenidos(Integer puntosObtenidos) { this.puntosObtenidos = puntosObtenidos; }

    public String getRetroalimentacion() { return retroalimentacion; }
    public void setRetroalimentacion(String retroalimentacion) { this.retroalimentacion = retroalimentacion; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public IntentoEvaluacion getIntento() { return intento; }
    public void setIntento(IntentoEvaluacion intento) { this.intento = intento; }

    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }

    // Métodos útiles
    public boolean isCalificada() {
        return esCorrecta != null || puntosObtenidos != null;
    }
}




