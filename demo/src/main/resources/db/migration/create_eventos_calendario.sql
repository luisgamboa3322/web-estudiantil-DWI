-- Script SQL para crear la tabla de eventos del calendario
-- Ejecutar este script en tu base de datos MySQL

CREATE TABLE IF NOT EXISTS eventos_calendario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL,
    student_id BIGINT NOT NULL,
    curso_id BIGINT,
    notificacion_enviada BOOLEAN DEFAULT FALSE,
    fecha_creacion DATETIME NOT NULL,
    
    CONSTRAINT fk_evento_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_evento_curso FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE SET NULL,
    
    INDEX idx_student_fecha (student_id, fecha_inicio),
    INDEX idx_tipo (tipo),
    INDEX idx_notificacion (notificacion_enviada, fecha_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Comentarios sobre la tabla
-- tipo: TAREA, EXAMEN, FORO, PERSONAL, REUNION, ESTUDIO, RECORDATORIO
-- color: Código hexadecimal del color (#RRGGBB)
-- notificacion_enviada: Para sistema de notificaciones futuro
