package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de rendimiento para la aplicación.
 * Incluye configuración de procesamiento asíncrono y thread pools.
 * 
 * Esta configuración solo se activa en el perfil de producción.
 */
@Configuration
@EnableAsync
@Profile("prod") // Solo en producción
public class PerformanceConfig {

    /**
     * Configuración del executor para tareas asíncronas.
     * 
     * Configuración del pool de threads:
     * - Core pool size: 5 threads
     * - Max pool size: 10 threads
     * - Queue capacity: 100 tareas
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Número de threads que siempre estarán activos
        executor.setCorePoolSize(5);

        // Número máximo de threads
        executor.setMaxPoolSize(10);

        // Capacidad de la cola de tareas
        executor.setQueueCapacity(100);

        // Prefijo para los nombres de los threads
        executor.setThreadNamePrefix("async-");

        // Esperar a que las tareas terminen antes de shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Tiempo máximo de espera para shutdown (en segundos)
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}
