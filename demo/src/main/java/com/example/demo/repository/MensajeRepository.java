package com.example.demo.repository;

import com.example.demo.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Obtener historial de mensajes de una conversación ordenados por fecha
    List<Mensaje> findByConversacionIdOrderByFechaEnvioAsc(String conversacionId);

    // Obtener todas las conversaciones únicas de un usuario
    @Query("SELECT DISTINCT m.conversacionId FROM Mensaje m WHERE m.remitenteId = :userId OR m.destinatarioId = :userId")
    List<String> findDistinctConversacionIdByUsuario(@Param("userId") Long userId);

    // Contar mensajes no leídos en una conversación para un destinatario específico
    Long countByConversacionIdAndDestinatarioIdAndLeidoFalse(String conversacionId, Long destinatarioId);

    // Obtener el último mensaje de una conversación
    Mensaje findFirstByConversacionIdOrderByFechaEnvioDesc(String conversacionId);
}
