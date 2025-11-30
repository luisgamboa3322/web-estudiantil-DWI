-- ============================================
-- Scripts SQL para Optimización de Base de Datos
-- Web Estudiantil
-- ============================================

-- Este archivo contiene scripts para crear índices y optimizar
-- el rendimiento de las consultas en la base de datos.

USE webestudiantil;

-- ============================================
-- ÍNDICES PARA TABLA: usuario
-- ============================================

-- Índice en email (usado frecuentemente para login)
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);

-- Índice en rol (usado para filtrar por tipo de usuario)
CREATE INDEX IF NOT EXISTS idx_usuario_rol ON usuario(rol);

-- Índice compuesto para búsquedas por nombre y rol
CREATE INDEX IF NOT EXISTS idx_usuario_nombre_rol ON usuario(nombre, rol);

-- ============================================
-- ÍNDICES PARA TABLA: curso
-- ============================================

-- Índice en profesor_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_curso_profesor ON curso(profesor_id);

-- Índice en nombre del curso (para búsquedas)
CREATE INDEX IF NOT EXISTS idx_curso_nombre ON curso(nombre);

-- Índice en estado del curso
CREATE INDEX IF NOT EXISTS idx_curso_estado ON curso(estado);

-- ============================================
-- ÍNDICES PARA TABLA: inscripcion
-- ============================================

-- Índice en estudiante_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_inscripcion_estudiante ON inscripcion(estudiante_id);

-- Índice en curso_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_inscripcion_curso ON inscripcion(curso_id);

-- Índice compuesto para consultas de inscripciones por estudiante y curso
CREATE INDEX IF NOT EXISTS idx_inscripcion_estudiante_curso ON inscripcion(estudiante_id, curso_id);

-- Índice en fecha de inscripción (para ordenamiento)
CREATE INDEX IF NOT EXISTS idx_inscripcion_fecha ON inscripcion(fecha_inscripcion);

-- ============================================
-- ÍNDICES PARA TABLA: actividad
-- ============================================

-- Índice en curso_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_actividad_curso ON actividad(curso_id);

-- Índice en tipo de actividad
CREATE INDEX IF NOT EXISTS idx_actividad_tipo ON actividad(tipo);

-- Índice en fecha de entrega (para consultas de actividades pendientes)
CREATE INDEX IF NOT EXISTS idx_actividad_fecha_entrega ON actividad(fecha_entrega);

-- Índice compuesto para consultas de actividades por curso y fecha
CREATE INDEX IF NOT EXISTS idx_actividad_curso_fecha ON actividad(curso_id, fecha_entrega);

-- ============================================
-- ÍNDICES PARA TABLA: entrega
-- ============================================

-- Índice en actividad_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_entrega_actividad ON entrega(actividad_id);

-- Índice en estudiante_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_entrega_estudiante ON entrega(estudiante_id);

-- Índice compuesto para consultas de entregas por estudiante y actividad
CREATE INDEX IF NOT EXISTS idx_entrega_estudiante_actividad ON entrega(estudiante_id, actividad_id);

-- Índice en fecha de entrega
CREATE INDEX IF NOT EXISTS idx_entrega_fecha ON entrega(fecha_entrega);

-- ============================================
-- ÍNDICES PARA TABLA: mensaje
-- ============================================

-- Índice en remitente_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_mensaje_remitente ON mensaje(remitente_id);

-- Índice en destinatario_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_mensaje_destinatario ON mensaje(destinatario_id);

-- Índice compuesto para consultas de mensajes entre usuarios
CREATE INDEX IF NOT EXISTS idx_mensaje_remitente_destinatario ON mensaje(remitente_id, destinatario_id);

-- Índice en fecha de envío (para ordenamiento)
CREATE INDEX IF NOT EXISTS idx_mensaje_fecha ON mensaje(fecha_envio);

-- Índice en estado de lectura
CREATE INDEX IF NOT EXISTS idx_mensaje_leido ON mensaje(leido);

-- ============================================
-- ÍNDICES PARA TABLA: evento_calendario
-- ============================================

-- Índice en usuario_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_evento_usuario ON evento_calendario(usuario_id);

-- Índice en fecha de inicio (para consultas de eventos por rango de fechas)
CREATE INDEX IF NOT EXISTS idx_evento_fecha_inicio ON evento_calendario(fecha_inicio);

-- Índice en fecha de fin
CREATE INDEX IF NOT EXISTS idx_evento_fecha_fin ON evento_calendario(fecha_fin);

-- Índice compuesto para consultas de eventos por usuario y fecha
CREATE INDEX IF NOT EXISTS idx_evento_usuario_fecha ON evento_calendario(usuario_id, fecha_inicio);

-- ============================================
-- ÍNDICES PARA TABLA: notificacion
-- ============================================

-- Índice en usuario_id (clave foránea)
CREATE INDEX IF NOT EXISTS idx_notificacion_usuario ON notificacion(usuario_id);

-- Índice en estado de lectura
CREATE INDEX IF NOT EXISTS idx_notificacion_leida ON notificacion(leida);

-- Índice en fecha de creación
CREATE INDEX IF NOT EXISTS idx_notificacion_fecha ON notificacion(fecha_creacion);

-- Índice compuesto para consultas de notificaciones no leídas por usuario
CREATE INDEX IF NOT EXISTS idx_notificacion_usuario_leida ON notificacion(usuario_id, leida);

-- ============================================
-- VERIFICACIÓN DE ÍNDICES CREADOS
-- ============================================

-- Mostrar todos los índices de la base de datos
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    INDEX_TYPE
FROM 
    INFORMATION_SCHEMA.STATISTICS
WHERE 
    TABLE_SCHEMA = 'webestudiantil'
    AND INDEX_NAME != 'PRIMARY'
ORDER BY 
    TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================

/*
1. Los índices mejoran la velocidad de las consultas SELECT, pero pueden
   ralentizar las operaciones INSERT, UPDATE y DELETE.

2. Usar índices en:
   - Claves foráneas (para JOINs)
   - Columnas usadas en WHERE
   - Columnas usadas en ORDER BY
   - Columnas usadas frecuentemente en búsquedas

3. No crear índices en:
   - Tablas muy pequeñas
   - Columnas que cambian frecuentemente
   - Columnas con baja cardinalidad (pocos valores únicos)

4. Monitorear el rendimiento de las consultas con:
   EXPLAIN SELECT ...

5. Estos índices se crean automáticamente si no existen (IF NOT EXISTS)
*/
