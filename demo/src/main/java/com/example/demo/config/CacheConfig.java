package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché para mejorar el rendimiento de la aplicación.
 * Utiliza Caffeine como implementación de caché en memoria.
 * 
 * Esta configuración solo se activa en el perfil de producción.
 */
@Configuration
@EnableCaching
@Profile("prod") // Solo en producción
public class CacheConfig {

    /**
     * Configuración del gestor de caché con Caffeine.
     * 
     * Políticas de caché:
     * - Tamaño máximo: 1000 entradas por caché
     * - Expiración: 1 hora después del último acceso
     * - Registro de estadísticas para monitoreo
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "cursos", // Caché de cursos
                "usuarios", // Caché de usuarios
                "estudiantes", // Caché de estudiantes
                "profesores", // Caché de profesores
                "inscripciones", // Caché de inscripciones
                "actividades", // Caché de actividades
                "mensajes" // Caché de mensajes
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Constructor de caché Caffeine con configuraciones optimizadas.
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                // Tamaño máximo de 1000 entradas
                .maximumSize(1000)
                // Expira después de 1 hora sin acceso
                .expireAfterAccess(1, TimeUnit.HOURS)
                // Expira después de 2 horas desde la escritura
                .expireAfterWrite(2, TimeUnit.HOURS)
                // Habilitar estadísticas para monitoreo
                .recordStats()
                // Tamaño inicial del caché
                .initialCapacity(100);
    }
}
