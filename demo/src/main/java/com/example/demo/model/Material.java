package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "materiales")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del material es obligatorio")
    private String nombre;

    @Column(nullable = true)
    private String fileName; // Nombre original del archivo

    @Column(nullable = true)
    private String fileType; // Tipo MIME del archivo (application/pdf, etc.)

    @Lob
    @Column(columnDefinition = "LONGBLOB", nullable = true)
    private byte[] fileData; // Datos binarios del archivo

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semana_id", nullable = false)
    @JsonIgnoreProperties({"curso", "materiales", "tareas"})
    private Semana semana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "cursos"})
    private Professor profesor;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Material() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Material(String nombre, String fileName, String fileType, byte[] fileData, String descripcion, Semana semana, Professor profesor) {
        this.nombre = nombre;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.descripcion = descripcion;
        this.semana = semana;
        this.profesor = profesor;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Semana getSemana() { return semana; }
    public void setSemana(Semana semana) { this.semana = semana; }

    public Professor getProfesor() { return profesor; }
    public void setProfesor(Professor profesor) { this.profesor = profesor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}