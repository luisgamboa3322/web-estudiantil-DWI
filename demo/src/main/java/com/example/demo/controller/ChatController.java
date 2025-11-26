package com.example.demo.controller;

import com.example.demo.dto.ContactoDTO;
import com.example.demo.dto.ConversacionDTO;
import com.example.demo.dto.MensajeDTO;
import com.example.demo.model.Curso;
import com.example.demo.model.Mensaje;
import com.example.demo.model.TipoRemitente;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.StudentCursoRepository;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private StudentCursoRepository studentCursoRepository;

    /**
     * Endpoint WebSocket para enviar mensajes privados
     */
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload MensajeDTO mensajeDTO) {
        // Guardar mensaje en la base de datos
        Mensaje mensaje = chatService.enviarMensaje(mensajeDTO);

        // Convertir a DTO para enviar
        MensajeDTO mensajeEnviado = convertirAMensajeDTO(mensaje);

        // Enviar al destinatario específico
        String destinatario = mensajeDTO.getDestinatarioId() + "_" + mensajeDTO.getTipoDestinatario().name();
        messagingTemplate.convertAndSendToUser(
                destinatario,
                "/queue/messages",
                mensajeEnviado);

        // También enviar al remitente para confirmación
        String remitente = mensajeDTO.getRemitenteId() + "_" + mensajeDTO.getTipoRemitente().name();
        messagingTemplate.convertAndSendToUser(
                remitente,
                "/queue/messages",
                mensajeEnviado);
    }

    /**
     * Obtener lista de cursos del usuario
     */
    @GetMapping("/cursos")
    public ResponseEntity<List<Map<String, Object>>> obtenerCursos(
            @RequestParam Long usuarioId,
            @RequestParam String tipo) {

        TipoRemitente tipoRemitente = TipoRemitente.valueOf(tipo.toUpperCase());
        List<Curso> cursos;

        if (tipoRemitente == TipoRemitente.ESTUDIANTE) {
            // Obtener cursos del estudiante
            cursos = studentCursoRepository.findByCursoId(usuarioId).stream()
                    .map(sc -> sc.getCurso())
                    .toList();
        } else {
            // Obtener cursos del profesor
            cursos = cursoRepository.findByProfesorId(usuarioId);
        }

        List<Map<String, Object>> cursosDTO = cursos.stream().map(curso -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", curso.getId());
            map.put("nombre", curso.getNombre());
            map.put("codigo", curso.getCodigo());
            map.put("descripcion", curso.getDescripcion());
            return map;
        }).toList();

        return ResponseEntity.ok(cursosDTO);
    }

    /**
     * Obtener contactos de un curso
     */
    @GetMapping("/contactos/{cursoId}")
    public ResponseEntity<List<ContactoDTO>> obtenerContactos(
            @PathVariable Long cursoId,
            @RequestParam Long usuarioId,
            @RequestParam String tipo) {

        TipoRemitente tipoRemitente = TipoRemitente.valueOf(tipo.toUpperCase());
        List<ContactoDTO> contactos = chatService.obtenerContactosPorCurso(cursoId, usuarioId, tipoRemitente);

        return ResponseEntity.ok(contactos);
    }

    /**
     * Obtener conversaciones activas del usuario
     */
    @GetMapping("/conversaciones")
    public ResponseEntity<List<ConversacionDTO>> obtenerConversaciones(
            @RequestParam Long usuarioId,
            @RequestParam String tipo) {

        TipoRemitente tipoRemitente = TipoRemitente.valueOf(tipo.toUpperCase());
        List<ConversacionDTO> conversaciones = chatService.obtenerConversacionesActivas(usuarioId, tipoRemitente);

        return ResponseEntity.ok(conversaciones);
    }

    /**
     * Obtener historial de mensajes de una conversación
     */
    @GetMapping("/mensajes/{conversacionId}")
    public ResponseEntity<List<MensajeDTO>> obtenerMensajes(@PathVariable String conversacionId) {
        List<MensajeDTO> mensajes = chatService.obtenerHistorialConversacion(conversacionId);
        return ResponseEntity.ok(mensajes);
    }

    /**
     * Marcar mensajes como leídos
     */
    @PostMapping("/marcar-leido/{conversacionId}")
    public ResponseEntity<Void> marcarComoLeido(
            @PathVariable String conversacionId,
            @RequestParam Long usuarioId) {

        chatService.marcarMensajesComoLeidos(conversacionId, usuarioId);
        return ResponseEntity.ok().build();
    }

    /**
     * Convertir Mensaje a MensajeDTO
     */
    private MensajeDTO convertirAMensajeDTO(Mensaje mensaje) {
        MensajeDTO dto = new MensajeDTO();
        dto.setConversacionId(mensaje.getConversacionId());
        dto.setRemitenteId(mensaje.getRemitenteId());
        dto.setTipoRemitente(mensaje.getTipoRemitente());
        dto.setDestinatarioId(mensaje.getDestinatarioId());
        dto.setTipoDestinatario(mensaje.getTipoDestinatario());
        dto.setContenido(mensaje.getContenido());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setLeido(mensaje.getLeido());
        return dto;
    }
}
