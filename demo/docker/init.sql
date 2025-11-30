-- ============================================
-- Script de Inicialización de Base de Datos
-- Web Estudiantil
-- ============================================

-- Este script se ejecuta automáticamente cuando se crea el contenedor de MySQL
-- Solo se ejecuta si la base de datos no existe

USE webestudiantil;

-- Mensaje de confirmación
SELECT 'Inicializando base de datos Web Estudiantil...' AS mensaje;

-- Crear índices para mejorar rendimiento
-- (Solo si las tablas ya existen por Hibernate)

-- Nota: Hibernate creará las tablas automáticamente con ddl-auto=update
-- Este script puede usarse para:
-- 1. Insertar datos iniciales
-- 2. Crear índices adicionales
-- 3. Configurar permisos adicionales

-- Ejemplo de datos iniciales (descomentar si es necesario)
/*
INSERT INTO usuario (nombre, email, password, rol) VALUES
('Administrador', 'admin@webestudiantil.com', '$2a$10$...', 'ADMIN'),
('Profesor Demo', 'profesor@webestudiantil.com', '$2a$10$...', 'PROFESOR'),
('Estudiante Demo', 'estudiante@webestudiantil.com', '$2a$10$...', 'ESTUDIANTE');
*/

-- Confirmar inicialización
SELECT 'Base de datos inicializada correctamente' AS mensaje;
