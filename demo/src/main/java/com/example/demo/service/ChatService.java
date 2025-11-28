package com.example.demo.service;

import com.example.demo.dto.ContactoDTO;
import com.example.demo.dto.ConversacionDTO;
import com.example.demo.dto.MensajeDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentCursoRepository studentCursoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Generar ID único de conversación entre dos usuarios
     */
    public String generarConversacionId(Long id1, TipoRemitente tipo1, Long id2, TipoRemitente tipo2) {
        // Ordenar para que siempre sea el mismo ID independientemente del orden
        String parte1 = id1 + "_" + tipo1.name();
        String parte2 = id2 + "_" + tipo2.name();

        if (parte1.compareTo(parte2) < 0) {
            return parte1 + "_" + parte2;
        } else {
            return parte2 + "_" + parte1;
        }
    }

    /**
     * Enviar y guardar mensaje privado
     */
    @Transactional
    public Mensaje enviarMensaje(MensajeDTO mensajeDTO) {
        // Generar conversación ID si no existe
        if (mensajeDTO.getConversacionId() == null || mensajeDTO.getConversacionId().isEmpty()) {
            String conversacionId = generarConversacionId(
                    mensajeDTO.getRemitenteId(),
                    mensajeDTO.getTipoRemitente(),
                    mensajeDTO.getDestinatarioId(),
                    mensajeDTO.getTipoDestinatario());
            mensajeDTO.setConversacionId(conversacionId);
        }

        // Crear entidad Mensaje
        Mensaje mensaje = new Mensaje();
        mensaje.setConversacionId(mensajeDTO.getConversacionId());
        mensaje.setRemitenteId(mensajeDTO.getRemitenteId());
        mensaje.setTipoRemitente(mensajeDTO.getTipoRemitente());
        mensaje.setDestinatarioId(mensajeDTO.getDestinatarioId());
        mensaje.setTipoDestinatario(mensajeDTO.getTipoDestinatario());
        mensaje.setContenido(mensajeDTO.getContenido());
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);

        return mensajeRepository.save(mensaje);
    }

    /**
     * Obtener historial de conversación
     */
    public List<MensajeDTO> obtenerHistorialConversacion(String conversacionId) {
        List<Mensaje> mensajes = mensajeRepository.findByConversacionIdOrderByFechaEnvioAsc(conversacionId);

        return mensajes.stream().map(this::convertirAMensajeDTO).collect(Collectors.toList());
    }

    /**
     * Obtener contactos disponibles por curso
     */
    public List<ContactoDTO> obtenerContactosPorCurso(Long cursoId, Long usuarioId, TipoRemitente tipoUsuario) {
        List<ContactoDTO> contactos = new ArrayList<>();

        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);
        if (cursoOpt.isEmpty()) {
            return contactos;
        }

        Curso curso = cursoOpt.get();

        // Si es estudiante, agregar al profesor y otros estudiantes
        if (tipoUsuario == TipoRemitente.ESTUDIANTE) {
            // Agregar profesor
            if (curso.getProfesor() != null) {
                ContactoDTO profesorDTO = new ContactoDTO(
                        curso.getProfesor().getId(),
                        curso.getProfesor().getNombre(),
                        TipoRemitente.PROFESOR,
                        curso.getProfesor().getEmail());
                contactos.add(profesorDTO);
            }

            // Agregar otros estudiantes del curso
            List<StudentCurso> inscripciones = studentCursoRepository.findByCursoId(cursoId);
            for (StudentCurso sc : inscripciones) {
                if (!sc.getStudent().getId().equals(usuarioId)) {
                    ContactoDTO estudianteDTO = new ContactoDTO(
                            sc.getStudent().getId(),
                            sc.getStudent().getNombre(),
                            TipoRemitente.ESTUDIANTE,
                            sc.getStudent().getEmail());
                    contactos.add(estudianteDTO);
                }
            }
        }
        // Si es profesor, agregar todos los estudiantes
        else if (tipoUsuario == TipoRemitente.PROFESOR) {
            List<StudentCurso> inscripciones = studentCursoRepository.findByCursoId(cursoId);
            for (StudentCurso sc : inscripciones) {
                ContactoDTO estudianteDTO = new ContactoDTO(
                        sc.getStudent().getId(),
                        sc.getStudent().getNombre(),
                        TipoRemitente.ESTUDIANTE,
                        sc.getStudent().getEmail());
                contactos.add(estudianteDTO);
            }
        }

        return contactos;
    }

    /**
     * Obtener conversaciones activas del usuario
     */
    public List<ConversacionDTO> obtenerConversacionesActivas(Long usuarioId, TipoRemitente tipoUsuario) {
        List<String> conversacionIds = mensajeRepository.findDistinctConversacionIdByUsuario(usuarioId);
        List<ConversacionDTO> conversaciones = new ArrayList<>();

        for (String conversacionId : conversacionIds) {
            Mensaje ultimoMensaje = mensajeRepository.findFirstByConversacionIdOrderByFechaEnvioDesc(conversacionId);

            if (ultimoMensaje != null) {
                // Determinar el contacto (la otra persona en la conversación)
                Long contactoId;
                TipoRemitente contactoTipo;

                if (ultimoMensaje.getRemitenteId().equals(usuarioId) &&
                        ultimoMensaje.getTipoRemitente() == tipoUsuario) {
                    contactoId = ultimoMensaje.getDestinatarioId();
                    contactoTipo = ultimoMensaje.getTipoDestinatario();
                } else {
                    contactoId = ultimoMensaje.getRemitenteId();
                    contactoTipo = ultimoMensaje.getTipoRemitente();
                }

                // Obtener nombre del contacto
                String contactoNombre = obtenerNombreUsuario(contactoId, contactoTipo);

                // Contar mensajes no leídos
                Long noLeidos = mensajeRepository.countByConversacionIdAndDestinatarioIdAndLeidoFalse(
                        conversacionId, usuarioId);

                ConversacionDTO conversacionDTO = new ConversacionDTO(
                        conversacionId, contactoId, contactoNombre, contactoTipo);
                conversacionDTO.setUltimoMensaje(ultimoMensaje.getContenido());
                conversacionDTO.setFechaUltimoMensaje(ultimoMensaje.getFechaEnvio());
                conversacionDTO.setMensajesNoLeidos(noLeidos);

                conversaciones.add(conversacionDTO);
            }
        }

        // Ordenar por fecha del último mensaje (más reciente primero)
        conversaciones.sort((c1, c2) -> c2.getFechaUltimoMensaje().compareTo(c1.getFechaUltimoMensaje()));

        return conversaciones;
    }

    /**
     * Marcar mensajes como leídos
     */
    @Transactional
    public void marcarMensajesComoLeidos(String conversacionId, Long usuarioId) {
        List<Mensaje> mensajes = mensajeRepository.findByConversacionIdOrderByFechaEnvioAsc(conversacionId);

        for (Mensaje mensaje : mensajes) {
            if (mensaje.getDestinatarioId().equals(usuarioId) && !mensaje.getLeido()) {
                mensaje.setLeido(true);
                mensajeRepository.save(mensaje);
            }
        }
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

        // Obtener nombres
        dto.setRemitenteNombre(obtenerNombreUsuario(mensaje.getRemitenteId(), mensaje.getTipoRemitente()));
        dto.setDestinatarioNombre(obtenerNombreUsuario(mensaje.getDestinatarioId(), mensaje.getTipoDestinatario()));

        return dto;
    }

    /**
     * Obtener nombre de usuario por ID y tipo
     */
    private String obtenerNombreUsuario(Long id, TipoRemitente tipo) {
        if (tipo == TipoRemitente.ESTUDIANTE) {
            return studentRepository.findById(id)
                    .map(Student::getNombre)
                    .orElse("Estudiante");
        } else {
            return professorRepository.findById(id)
                    .map(Professor::getNombre)
                    .orElse("Profesor");
        }
    }

    /**
     * Obtener email de usuario por ID y tipo
     */
    public String obtenerEmailUsuario(Long id, TipoRemitente tipo) {
        if (tipo == TipoRemitente.ESTUDIANTE) {
            return studentRepository.findById(id)
                    .map(Student::getEmail)
                    .orElse(null);
        } else {
            return professorRepository.findById(id)
                    .map(Professor::getEmail)
                    .orElse(null);
        }
    }
}
