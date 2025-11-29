package com.example.demo.service;

import com.example.demo.dto.EventoCalendarioDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

    @Autowired
    private EventoCalendarioRepository eventoCalendarioRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private StudentCursoRepository studentCursoRepository;

    /**
     * Obtiene todos los eventos del calendario de un estudiante
     * Incluye: eventos personales, tareas y evaluaciones de sus cursos
     */
    @Transactional(readOnly = true)
    public List<EventoCalendarioDTO> obtenerEventosEstudiante(Long studentId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        Student estudiante = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        List<EventoCalendarioDTO> eventos = new ArrayList<>();

        // 1. Obtener eventos personales del estudiante
        List<EventoCalendario> eventosPersonales = eventoCalendarioRepository
                .findByEstudianteAndFechaRange(estudiante, fechaInicio, fechaFin);

        for (EventoCalendario evento : eventosPersonales) {
            eventos.add(convertirADTO(evento, true));
        }

        // 2. Obtener cursos del estudiante
        List<StudentCurso> cursosEstudiante = studentCursoRepository.findByStudentId(estudiante.getId());

        // 3. Obtener tareas de los cursos del estudiante
        for (StudentCurso sc : cursosEstudiante) {
            Curso curso = sc.getCurso();

            // Obtener tareas del curso en el rango de fechas
            List<Tarea> tareas = tareaRepository.findBySemana_CursoAndFechaLimiteBetween(
                    curso, fechaInicio, fechaFin);

            for (Tarea tarea : tareas) {
                eventos.add(convertirTareaADTO(tarea, curso));
            }

            // Obtener evaluaciones del curso en el rango de fechas
            List<Evaluacion> evaluaciones = evaluacionRepository
                    .findBySemana_CursoAndFechaLimiteBetween(curso, fechaInicio, fechaFin);

            for (Evaluacion evaluacion : evaluaciones) {
                eventos.add(convertirEvaluacionADTO(evaluacion, curso));
            }
        }

        // Ordenar eventos por fecha de inicio
        eventos.sort((e1, e2) -> e1.getFechaInicio().compareTo(e2.getFechaInicio()));

        return eventos;
    }

    /**
     * Crear un evento personal
     */
    @Transactional
    public EventoCalendarioDTO crearEventoPersonal(Long studentId, EventoCalendarioDTO dto) {
        Student estudiante = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        EventoCalendario evento = new EventoCalendario();
        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setTipo(dto.getTipo());
        evento.setColor(dto.getColor());
        evento.setEstudiante(estudiante);

        if (dto.getCursoId() != null) {
            Curso curso = cursoRepository.findById(dto.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            evento.setCurso(curso);
        }

        EventoCalendario eventoGuardado = eventoCalendarioRepository.save(evento);
        return convertirADTO(eventoGuardado, true);
    }

    /**
     * Actualizar un evento personal
     */
    @Transactional
    public EventoCalendarioDTO actualizarEventoPersonal(Long eventoId, EventoCalendarioDTO dto) {
        EventoCalendario evento = eventoCalendarioRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setTipo(dto.getTipo());
        evento.setColor(dto.getColor());

        EventoCalendario eventoActualizado = eventoCalendarioRepository.save(evento);
        return convertirADTO(eventoActualizado, true);
    }

    /**
     * Eliminar un evento personal
     */
    @Transactional
    public void eliminarEventoPersonal(Long eventoId) {
        eventoCalendarioRepository.deleteById(eventoId);
    }

    /**
     * Obtener eventos próximos (para notificaciones)
     */
    @Transactional(readOnly = true)
    public List<EventoCalendarioDTO> obtenerEventosProximos(Long studentId, int dias) {
        Student estudiante = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(dias);

        List<EventoCalendario> eventosProximos = eventoCalendarioRepository
                .findProximosEventosSinNotificar(estudiante, ahora, limite);

        return eventosProximos.stream()
                .map(e -> convertirADTO(e, true))
                .collect(Collectors.toList());
    }

    // Métodos auxiliares de conversión

    private EventoCalendarioDTO convertirADTO(EventoCalendario evento, boolean esEditable) {
        EventoCalendarioDTO dto = new EventoCalendarioDTO();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setFechaInicio(evento.getFechaInicio());
        dto.setFechaFin(evento.getFechaFin());
        dto.setTipo(evento.getTipo());
        dto.setColor(evento.getColor());
        dto.setEsEditable(esEditable);

        if (evento.getCurso() != null) {
            dto.setCursoId(evento.getCurso().getId());
            dto.setCursoNombre(evento.getCurso().getNombre());
        }

        return dto;
    }

    private EventoCalendarioDTO convertirTareaADTO(Tarea tarea, Curso curso) {
        EventoCalendarioDTO dto = new EventoCalendarioDTO();
        dto.setId(tarea.getId());
        dto.setTitulo(tarea.getTitulo());
        dto.setDescripcion(tarea.getDescripcion());
        dto.setFechaInicio(tarea.getFechaLimite().minusHours(1)); // 1 hora antes como inicio
        dto.setFechaFin(tarea.getFechaLimite());
        dto.setTipo(TipoEvento.TAREA);
        dto.setColor("#3B82F6"); // Azul para tareas
        dto.setCursoId(curso.getId());
        dto.setCursoNombre(curso.getNombre());
        dto.setEsEditable(false); // Las tareas del sistema no son editables
        return dto;
    }

    private EventoCalendarioDTO convertirEvaluacionADTO(Evaluacion evaluacion, Curso curso) {
        EventoCalendarioDTO dto = new EventoCalendarioDTO();
        dto.setId(evaluacion.getId());
        dto.setTitulo(evaluacion.getTitulo());
        dto.setDescripcion(evaluacion.getDescripcion());
        dto.setFechaInicio(evaluacion.getFechaInicio());
        dto.setFechaFin(evaluacion.getFechaLimite());
        dto.setTipo(TipoEvento.EXAMEN);
        dto.setColor("#EF4444"); // Rojo para exámenes
        dto.setCursoId(curso.getId());
        dto.setCursoNombre(curso.getNombre());
        dto.setEsEditable(false); // Las evaluaciones del sistema no son editables
        return dto;
    }
}
