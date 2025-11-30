package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private IntentoEvaluacionRepository intentoEvaluacionRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private RespuestaEstudianteRepository respuestaEstudianteRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private OpcionRespuestaRepository opcionRespuestaRepository;

    @Autowired
    private SemanaService semanaService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private StudentService studentService;

    public Optional<Evaluacion> findById(Long id) {
        return evaluacionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Evaluacion> findBySemanaId(Long semanaId) {
        return evaluacionRepository.findBySemanaIdOrderByFechaCreacion(semanaId);
    }

    @Transactional(readOnly = true)
    public List<Evaluacion> findByCurso(Curso curso) {
        return evaluacionRepository.findBySemana_Curso(curso);
    }

    @Transactional(readOnly = true)
    public List<IntentoEvaluacion> getIntentosByEvaluacionIdAndEstudianteId(Long evaluacionId, Long estudianteId) {
        List<IntentoEvaluacion> intentos = intentoEvaluacionRepository.findByEvaluacionIdAndEstudianteId(evaluacionId,
                estudianteId);
        // Inicializar relaciones lazy
        for (IntentoEvaluacion intento : intentos) {
            Hibernate.initialize(intento.getEstudiante());
            Hibernate.initialize(intento.getCalificacion());
            Hibernate.initialize(intento.getEvaluacion());
        }
        return intentos;
    }

    @Transactional
    public Evaluacion createEvaluacion(Long semanaId, String titulo, String descripcion, TipoEvaluacion tipo,
            LocalDateTime fechaInicio, LocalDateTime fechaLimite,
            Integer tiempoLimiteMinutos, Integer intentosMaximos,
            int puntosMaximos, Boolean mostrarResultadosInmediatos,
            Boolean permitirRevisarRespuestas, Long profesorId) {
        Semana semana = semanaService.findById(semanaId)
                .orElseThrow(() -> new IllegalArgumentException("Semana no encontrada"));
        Professor profesor = professorService.getById(profesorId)
                .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setTitulo(titulo);
        evaluacion.setDescripcion(descripcion);
        evaluacion.setTipo(tipo);
        evaluacion.setFechaInicio(fechaInicio);
        evaluacion.setFechaLimite(fechaLimite);
        evaluacion.setTiempoLimiteMinutos(tiempoLimiteMinutos);
        evaluacion.setIntentosMaximos(intentosMaximos != null ? intentosMaximos : 1);
        evaluacion.setPuntosMaximos(puntosMaximos);
        evaluacion.setMostrarResultadosInmediatos(
                mostrarResultadosInmediatos != null ? mostrarResultadosInmediatos : false);
        evaluacion.setPermitirRevisarRespuestas(permitirRevisarRespuestas != null ? permitirRevisarRespuestas : false);
        evaluacion.setSemana(semana);
        evaluacion.setProfesor(profesor);
        evaluacion.setEstado(EstadoEvaluacion.BORRADOR);

        return evaluacionRepository.save(evaluacion);
    }

    @Transactional
    public Evaluacion publicarEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        if (evaluacion.getPreguntas().isEmpty()) {
            throw new IllegalStateException("No se puede publicar una evaluación sin preguntas");
        }

        evaluacion.setEstado(EstadoEvaluacion.PUBLICADA);
        return evaluacionRepository.save(evaluacion);
    }

    @Transactional
    public void deleteEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        evaluacionRepository.delete(evaluacion);
    }

    @Transactional
    public IntentoEvaluacion iniciarIntento(Long evaluacionId, Long estudianteId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        // Verificar que la evaluación esté publicada o en curso
        if (evaluacion.getEstado() != EstadoEvaluacion.PUBLICADA &&
                evaluacion.getEstado() != EstadoEvaluacion.EN_CURSO) {
            throw new IllegalStateException("La evaluación no está disponible");
        }

        // Verificar fechas
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(evaluacion.getFechaInicio())) {
            throw new IllegalStateException("La evaluación aún no ha comenzado");
        }
        if (now.isAfter(evaluacion.getFechaLimite())) {
            throw new IllegalStateException("La evaluación ya ha vencido");
        }

        // Buscar intentos en progreso
        List<IntentoEvaluacion> intentosEnProgreso = intentoEvaluacionRepository
                .findByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId)
                .stream()
                .filter(i -> i.getEstado() == EstadoIntento.EN_PROGRESO)
                .collect(Collectors.toList());

        if (!intentosEnProgreso.isEmpty()) {
            return intentosEnProgreso.get(0); // Retornar el intento en progreso
        }

        // Contar intentos completados
        List<IntentoEvaluacion> intentosCompletados = intentoEvaluacionRepository
                .findByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId)
                .stream()
                .filter(i -> i.getEstado() == EstadoIntento.COMPLETADO || i.getEstado() == EstadoIntento.CALIFICADO)
                .collect(Collectors.toList());

        if (intentosCompletados.size() >= evaluacion.getIntentosMaximos()) {
            throw new IllegalStateException("Has alcanzado el número máximo de intentos");
        }

        // Crear nuevo intento
        Integer numeroIntento = intentosCompletados.size() + 1;
        Student estudiante = studentService.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        IntentoEvaluacion intento = new IntentoEvaluacion();
        intento.setNumeroIntento(numeroIntento);
        intento.setEvaluacion(evaluacion);
        intento.setEstudiante(estudiante);
        intento.setEstado(EstadoIntento.EN_PROGRESO);
        intento.setFechaInicio(LocalDateTime.now());

        // Calcular fecha límite del intento si hay tiempo límite
        if (evaluacion.getTiempoLimiteMinutos() != null) {
            intento.setFechaLimiteIntento(intento.getFechaInicio().plusMinutes(evaluacion.getTiempoLimiteMinutos()));
        }

        return intentoEvaluacionRepository.save(intento);
    }

    @Transactional(readOnly = true)
    public Optional<IntentoEvaluacion> findIntentoById(Long intentoId) {
        Optional<IntentoEvaluacion> intento = intentoEvaluacionRepository.findById(intentoId);
        if (intento.isPresent()) {
            Hibernate.initialize(intento.get().getEvaluacion());
            Hibernate.initialize(intento.get().getEvaluacion().getPreguntas());
            for (Pregunta pregunta : intento.get().getEvaluacion().getPreguntas()) {
                Hibernate.initialize(pregunta.getOpciones());
            }
            Hibernate.initialize(intento.get().getRespuestas());
        }
        return intento;
    }

    @Transactional
    public RespuestaEstudiante guardarRespuesta(Long intentoId, Long preguntaId, String respuestaTexto,
            Long opcionSeleccionadaId, String opcionesOrdenadas) {
        IntentoEvaluacion intento = intentoEvaluacionRepository.findById(intentoId)
                .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));

        if (intento.getEstado() != EstadoIntento.EN_PROGRESO) {
            throw new IllegalStateException("No se puede modificar un intento que ya está completado");
        }

        Pregunta pregunta = preguntaRepository.findById(preguntaId)
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));

        // Buscar respuesta existente o crear nueva
        Optional<RespuestaEstudiante> respuestaOpt = respuestaEstudianteRepository
                .findByIntentoIdAndPreguntaId(intentoId, preguntaId);

        RespuestaEstudiante respuesta;
        if (respuestaOpt.isPresent()) {
            respuesta = respuestaOpt.get();
        } else {
            respuesta = new RespuestaEstudiante(intento, pregunta);
        }

        respuesta.setRespuestaTexto(respuestaTexto);
        respuesta.setOpcionSeleccionadaId(opcionSeleccionadaId);
        respuesta.setOpcionesOrdenadas(opcionesOrdenadas);
        respuesta.setFechaRespuesta(LocalDateTime.now());

        return respuestaEstudianteRepository.save(respuesta);
    }

    @Transactional
    public IntentoEvaluacion finalizarIntento(Long intentoId) {
        IntentoEvaluacion intento = intentoEvaluacionRepository.findById(intentoId)
                .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));

        if (intento.getEstado() != EstadoIntento.EN_PROGRESO) {
            throw new IllegalStateException("El intento ya está finalizado");
        }

        // Inicializar relaciones necesarias antes de procesar
        Hibernate.initialize(intento.getEvaluacion());
        Hibernate.initialize(intento.getEstudiante());
        Hibernate.initialize(intento.getRespuestas());

        intento.setEstado(EstadoIntento.COMPLETADO);
        intento.setFechaFin(LocalDateTime.now());

        // Calificar automáticamente si es posible
        calificarIntentoAutomaticamente(intento);

        // Guardar y refrescar para asegurar que todas las relaciones estén cargadas
        IntentoEvaluacion intentoGuardado = intentoEvaluacionRepository.save(intento);
        intentoEvaluacionRepository.flush(); // Forzar flush para asegurar que se guarde

        return intentoGuardado;
    }

    @Transactional
    private void calificarIntentoAutomaticamente(IntentoEvaluacion intento) {
        List<RespuestaEstudiante> respuestas = respuestaEstudianteRepository.findByIntentoId(intento.getId());

        int puntosTotales = 0;
        int puntosMaximos = 0;

        for (RespuestaEstudiante respuesta : respuestas) {
            // Inicializar relaciones lazy para evitar problemas
            Hibernate.initialize(respuesta.getPregunta());
            Pregunta pregunta = respuesta.getPregunta();
            if (pregunta == null)
                continue;

            puntosMaximos += pregunta.getPuntos();

            // Inicializar opciones si es necesario
            if (pregunta.getTipo() == TipoPregunta.COMPLETAR ||
                    pregunta.getTipo() == TipoPregunta.OPCION_MULTIPLE ||
                    pregunta.getTipo() == TipoPregunta.VERDADERO_FALSO) {
                Hibernate.initialize(pregunta.getOpciones());
            }

            // Calificar según el tipo de pregunta
            if (pregunta.getTipo() == TipoPregunta.OPCION_MULTIPLE ||
                    pregunta.getTipo() == TipoPregunta.VERDADERO_FALSO) {

                if (respuesta.getOpcionSeleccionadaId() != null) {
                    Optional<OpcionRespuesta> opcionOpt = opcionRespuestaRepository
                            .findById(respuesta.getOpcionSeleccionadaId());
                    if (opcionOpt.isPresent() && opcionOpt.get().getEsCorrecta()) {
                        respuesta.setEsCorrecta(true);
                        respuesta.setPuntosObtenidos(pregunta.getPuntos());
                        puntosTotales += pregunta.getPuntos();
                    } else {
                        respuesta.setEsCorrecta(false);
                        respuesta.setPuntosObtenidos(0);
                    }
                }
            } else if (pregunta.getTipo() == TipoPregunta.COMPLETAR) {
                // Para completar, comparar texto (case-insensitive, sin espacios extra)
                if (respuesta.getRespuestaTexto() != null && pregunta.getOpciones().size() > 0) {
                    String respuestaNormalizada = respuesta.getRespuestaTexto().trim().toLowerCase();
                    String respuestaCorrecta = pregunta.getOpciones().get(0).getTexto().trim().toLowerCase();
                    if (respuestaNormalizada.equals(respuestaCorrecta)) {
                        respuesta.setEsCorrecta(true);
                        respuesta.setPuntosObtenidos(pregunta.getPuntos());
                        puntosTotales += pregunta.getPuntos();
                    } else {
                        respuesta.setEsCorrecta(false);
                        respuesta.setPuntosObtenidos(0);
                    }
                }
            }
            // Para DESARROLLO y ORDENAR, no se califica automáticamente

            respuestaEstudianteRepository.save(respuesta);
        }

        // Calcular calificación final
        double calificacionFinal = puntosMaximos > 0
                ? (double) puntosTotales / puntosMaximos * 100.0
                : 0.0;

        // Crear o actualizar calificación
        Optional<Calificacion> calificacionOpt = calificacionRepository.findByIntentoId(intento.getId());
        Calificacion calificacion;
        if (calificacionOpt.isPresent()) {
            calificacion = calificacionOpt.get();
        } else {
            calificacion = new Calificacion();
            calificacion.setIntento(intento);
        }

        calificacion.setCalificacion(calificacionFinal);
        calificacion.setCalificacionAutomatica(true);
        calificacion.setFechaCalificacion(LocalDateTime.now());

        calificacionRepository.save(calificacion);

        // Actualizar estado del intento
        intento.setEstado(EstadoIntento.CALIFICADO);
    }

    @Transactional(readOnly = true)
    public List<Pregunta> getPreguntasByEvaluacionId(Long evaluacionId) {
        List<Pregunta> preguntas = preguntaRepository.findByEvaluacionIdOrderByOrden(evaluacionId);
        for (Pregunta pregunta : preguntas) {
            Hibernate.initialize(pregunta.getOpciones());
        }
        return preguntas;
    }

    @Transactional
    public Pregunta createPregunta(Long evaluacionId, String enunciado, TipoPregunta tipo, int puntos,
            List<Map<String, Object>> opcionesData) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        // Obtener el siguiente orden
        List<Pregunta> preguntasExistentes = preguntaRepository.findByEvaluacionIdOrderByOrden(evaluacionId);
        int siguienteOrden = preguntasExistentes.size() + 1;

        Pregunta pregunta = new Pregunta();
        pregunta.setEnunciado(enunciado);
        pregunta.setTipo(tipo);
        pregunta.setPuntos(puntos);
        pregunta.setEvaluacion(evaluacion);
        pregunta.setOrden(siguienteOrden);

        pregunta = preguntaRepository.save(pregunta);

        // Crear opciones si es necesario
        if (opcionesData != null && !opcionesData.isEmpty()) {
            for (Map<String, Object> opcionData : opcionesData) {
                OpcionRespuesta opcion = new OpcionRespuesta();
                opcion.setTexto((String) opcionData.get("texto"));
                opcion.setEsCorrecta((Boolean) opcionData.get("esCorrecta"));
                opcion.setOrden((Integer) opcionData.get("orden"));
                opcion.setPregunta(pregunta);
                opcionRespuestaRepository.save(opcion);
            }
        }

        return pregunta;
    }

    @Transactional
    public void deletePregunta(Long preguntaId) {
        Pregunta pregunta = preguntaRepository.findById(preguntaId)
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));
        preguntaRepository.delete(pregunta);
    }
}
