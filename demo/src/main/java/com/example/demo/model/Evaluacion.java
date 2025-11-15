package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluaciones")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título de la evaluación es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El tipo de evaluación es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoEvaluacion tipo;

    @NotNull(message = "El estado de la evaluación es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoEvaluacion estado = EstadoEvaluacion.BORRADOR;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha límite es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaLimite;

    @Min(value = 1, message = "El tiempo límite debe ser mayor a 0")
    private Integer tiempoLimiteMinutos; // Tiempo límite en minutos (null = sin límite)

    @Min(value = 1, message = "Los intentos máximos deben ser mayor a 0")
    private Integer intentosMaximos = 1; // Número máximo de intentos permitidos

    @Min(value = 0, message = "Los puntos máximos no pueden ser negativos")
    private int puntosMaximos;

    private Boolean mostrarResultadosInmediatos = false; // Si se muestran resultados inmediatamente después de completar
    private Boolean permitirRevisarRespuestas = false; // Si se permite revisar respuestas después de completar

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semana_id", nullable = false)
    @JsonIgnoreProperties({"curso", "materiales", "tareas", "evaluaciones"})
    private Semana semana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "cursos"})
    private Professor profesor;

    @NotNull(message = "La fecha de creación es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"evaluacion"})
    private List<Pregunta> preguntas = new ArrayList<>();

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"evaluacion", "estudiante"})
    private List<IntentoEvaluacion> intentos = new ArrayList<>();

    // Constructor vacío
    public Evaluacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Evaluacion(String titulo, String descripcion, TipoEvaluacion tipo, 
                     LocalDateTime fechaInicio, LocalDateTime fechaLimite, 
                     Integer tiempoLimiteMinutos, Integer intentosMaximos, 
                     int puntosMaximos, Semana semana, Professor profesor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaLimite = fechaLimite;
        this.tiempoLimiteMinutos = tiempoLimiteMinutos;
        this.intentosMaximos = intentosMaximos;
        this.puntosMaximos = puntosMaximos;
        this.semana = semana;
        this.profesor = profesor;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoEvaluacion.BORRADOR;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoEvaluacion getTipo() { return tipo; }
    public void setTipo(TipoEvaluacion tipo) { this.tipo = tipo; }

    public EstadoEvaluacion getEstado() { return estado; }
    public void setEstado(EstadoEvaluacion estado) { this.estado = estado; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }

    public Integer getTiempoLimiteMinutos() { return tiempoLimiteMinutos; }
    public void setTiempoLimiteMinutos(Integer tiempoLimiteMinutos) { this.tiempoLimiteMinutos = tiempoLimiteMinutos; }

    public Integer getIntentosMaximos() { return intentosMaximos; }
    public void setIntentosMaximos(Integer intentosMaximos) { this.intentosMaximos = intentosMaximos; }

    public int getPuntosMaximos() { return puntosMaximos; }
    public void setPuntosMaximos(int puntosMaximos) { this.puntosMaximos = puntosMaximos; }

    public Boolean getMostrarResultadosInmediatos() { return mostrarResultadosInmediatos; }
    public void setMostrarResultadosInmediatos(Boolean mostrarResultadosInmediatos) { this.mostrarResultadosInmediatos = mostrarResultadosInmediatos; }

    public Boolean getPermitirRevisarRespuestas() { return permitirRevisarRespuestas; }
    public void setPermitirRevisarRespuestas(Boolean permitirRevisarRespuestas) { this.permitirRevisarRespuestas = permitirRevisarRespuestas; }

    @JsonIgnore
    public Semana getSemana() { return semana; }
    public void setSemana(Semana semana) { this.semana = semana; }

    @JsonIgnore
    public Professor getProfesor() { return profesor; }
    public void setProfesor(Professor profesor) { this.profesor = profesor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<Pregunta> getPreguntas() { return preguntas; }
    public void setPreguntas(List<Pregunta> preguntas) { this.preguntas = preguntas; }

    public List<IntentoEvaluacion> getIntentos() { return intentos; }
    public void setIntentos(List<IntentoEvaluacion> intentos) { this.intentos = intentos; }

    // Métodos útiles
    public boolean isActiva() {
        LocalDateTime now = LocalDateTime.now();
        return estado == EstadoEvaluacion.PUBLICADA || 
               estado == EstadoEvaluacion.EN_CURSO &&
               now.isAfter(fechaInicio) && now.isBefore(fechaLimite);
    }

    public boolean puedeIniciar() {
        LocalDateTime now = LocalDateTime.now();
        return estado == EstadoEvaluacion.PUBLICADA || estado == EstadoEvaluacion.EN_CURSO &&
               now.isAfter(fechaInicio) && now.isBefore(fechaLimite);
    }
}

