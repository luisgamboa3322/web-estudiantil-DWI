package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private OpcionRespuestaRepository opcionRespuestaRepository;

    @Autowired
    private IntentoEvaluacionRepository intentoEvaluacionRepository;

    @Autowired
    private RespuestaEstudianteRepository respuestaEstudianteRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private com.example.demo.repository.StudentRepository studentRepository;

    // ===========================
    // CRUD EVALUACIONES
    // ===========================
    public List<Evaluacion> findAll() {
        return evaluacionRepository.findAll();
    }

    public Optional<Evaluacion> findById(Long id) {
        return evaluacionRepository.findById(id);
    }

    public List<Evaluacion> findBySemanaId(Long semanaId) {
        return evaluacionRepository.findBySemanaIdOrderByFechaCreacion(semanaId);
    }

    public List<Evaluacion> findByProfesor(Professor profesor) {
        return evaluacionRepository.findByProfesor(profesor);
    }

    public List<Evaluacion> findByEstado(EstadoEvaluacion estado) {
        return evaluacionRepository.findByEstado(estado);
    }

    @Transactional
    public Evaluacion save(Evaluacion evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    @Transactional
    public Evaluacion createEvaluacion(String titulo, String descripcion, TipoEvaluacion tipo,
                                      LocalDateTime fechaInicio, LocalDateTime fechaLimite,
                                      Integer tiempoLimiteMinutos, Integer intentosMaximos,
                                      int puntosMaximos, Semana semana, Professor profesor) {
        Evaluacion evaluacion = new Evaluacion(titulo, descripcion, tipo, fechaInicio, fechaLimite,
                                               tiempoLimiteMinutos, intentosMaximos, puntosMaximos,
                                               semana, profesor);
        return save(evaluacion);
    }

    @Transactional
    public Evaluacion update(Long id, Evaluacion evaluacionDetails) {
        Evaluacion evaluacion = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        
        evaluacion.setTitulo(evaluacionDetails.getTitulo());
        evaluacion.setDescripcion(evaluacionDetails.getDescripcion());
        evaluacion.setTipo(evaluacionDetails.getTipo());
        evaluacion.setFechaInicio(evaluacionDetails.getFechaInicio());
        evaluacion.setFechaLimite(evaluacionDetails.getFechaLimite());
        evaluacion.setTiempoLimiteMinutos(evaluacionDetails.getTiempoLimiteMinutos());
        evaluacion.setIntentosMaximos(evaluacionDetails.getIntentosMaximos());
        evaluacion.setPuntosMaximos(evaluacionDetails.getPuntosMaximos());
        evaluacion.setMostrarResultadosInmediatos(evaluacionDetails.getMostrarResultadosInmediatos());
        evaluacion.setPermitirRevisarRespuestas(evaluacionDetails.getPermitirRevisarRespuestas());
        
        return save(evaluacion);
    }

    @Transactional
    public void delete(Long id) {
        if (!evaluacionRepository.existsById(id)) {
            throw new IllegalArgumentException("Evaluación no encontrada");
        }
        evaluacionRepository.deleteById(id);
    }

    @Transactional
    public Evaluacion publicarEvaluacion(Long id) {
        Evaluacion evaluacion = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        
        if (evaluacion.getPreguntas().isEmpty()) {
            throw new IllegalStateException("No se puede publicar una evaluación sin preguntas");
        }
        
        evaluacion.setEstado(EstadoEvaluacion.PUBLICADA);
        return save(evaluacion);
    }

    @Transactional
    public Evaluacion cerrarEvaluacion(Long id) {
        Evaluacion evaluacion = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        
        evaluacion.setEstado(EstadoEvaluacion.CERRADA);
        return save(evaluacion);
    }

    // ===========================
    // GESTIÓN DE PREGUNTAS
    // ===========================
    @Transactional
    public Pregunta agregarPregunta(Long evaluacionId, String enunciado, TipoPregunta tipo, int puntos) {
        Evaluacion evaluacion = findById(evaluacionId)
            .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        
        Pregunta pregunta = new Pregunta(enunciado, tipo, puntos, evaluacion);
        pregunta.setOrden(evaluacion.getPreguntas().size() + 1);
        
        Pregunta saved = preguntaRepository.save(pregunta);
        evaluacion.getPreguntas().add(saved);
        
        return saved;
    }

    @Transactional
    public OpcionRespuesta agregarOpcion(Long preguntaId, String texto, Boolean esCorrecta, Integer orden) {
        Pregunta pregunta = preguntaRepository.findById(preguntaId)
            .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));
        
        OpcionRespuesta opcion = new OpcionRespuesta(texto, esCorrecta, orden, pregunta);
        OpcionRespuesta saved = opcionRespuestaRepository.save(opcion);
        pregunta.getOpciones().add(saved);
        
        return saved;
    }

    public List<Pregunta> getPreguntasByEvaluacionId(Long evaluacionId) {
        return preguntaRepository.findByEvaluacionIdOrderByOrden(evaluacionId);
    }

    public List<OpcionRespuesta> getOpcionesByPreguntaId(Long preguntaId) {
        return opcionRespuestaRepository.findByPreguntaIdOrderByOrden(preguntaId);
    }

    @Transactional
    public void eliminarPregunta(Long preguntaId) {
        preguntaRepository.deleteById(preguntaId);
    }

    // ===========================
    // GESTIÓN DE INTENTOS
    // ===========================
    @Transactional
    public IntentoEvaluacion iniciarIntento(Long evaluacionId, Long estudianteId) {
        Evaluacion evaluacion = findById(evaluacionId)
            .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        
        Student estudiante = studentRepository.findById(estudianteId)
            .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
        
        // Verificar si puede iniciar un nuevo intento
        int intentosActuales = intentoEvaluacionRepository.countByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId);
        if (intentosActuales >= evaluacion.getIntentosMaximos()) {
            throw new IllegalStateException("Has alcanzado el número máximo de intentos permitidos");
        }
        
        // Verificar si hay un intento en progreso
        List<IntentoEvaluacion> intentosEnProgreso = intentoEvaluacionRepository
            .findByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId)
            .stream()
            .filter(i -> i.getEstado() == EstadoIntento.EN_PROGRESO)
            .toList();
        
        if (!intentosEnProgreso.isEmpty()) {
            throw new IllegalStateException("Ya tienes un intento en progreso");
        }
        
        // Verificar que la evaluación esté disponible
        if (!evaluacion.puedeIniciar()) {
            throw new IllegalStateException("La evaluación no está disponible en este momento");
        }
        
        Integer numeroIntento = intentosActuales + 1;
        IntentoEvaluacion intento = new IntentoEvaluacion(numeroIntento, evaluacion, estudiante);
        
        return intentoEvaluacionRepository.save(intento);
    }

    public List<IntentoEvaluacion> getIntentosByEvaluacionIdAndEstudianteId(Long evaluacionId, Long estudianteId) {
        return intentoEvaluacionRepository.findByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId);
    }

    public Optional<IntentoEvaluacion> getIntentoEnProgreso(Long evaluacionId, Long estudianteId) {
        return intentoEvaluacionRepository
            .findByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId)
            .stream()
            .filter(i -> i.getEstado() == EstadoIntento.EN_PROGRESO)
            .findFirst();
    }

    public Optional<IntentoEvaluacion> findIntentoById(Long intentoId) {
        return intentoEvaluacionRepository.findById(intentoId);
    }

    @Transactional
    public IntentoEvaluacion finalizarIntento(Long intentoId) {
        IntentoEvaluacion intento = intentoEvaluacionRepository.findById(intentoId)
            .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));
        
        if (intento.isExpirado()) {
            intento.setEstado(EstadoIntento.EXPIRADO);
        } else {
            intento.setEstado(EstadoIntento.COMPLETADO);
        }
        
        intento.setFechaFin(LocalDateTime.now());
        
        // Calificar automáticamente si es posible
        calificarIntentoAutomaticamente(intentoId);
        
        return intentoEvaluacionRepository.save(intento);
    }

    // ===========================
    // GESTIÓN DE RESPUESTAS
    // ===========================
    @Transactional
    public RespuestaEstudiante guardarRespuesta(Long intentoId, Long preguntaId, 
                                                String respuestaTexto, Long opcionSeleccionadaId,
                                                String opcionesOrdenadas) {
        IntentoEvaluacion intento = intentoEvaluacionRepository.findById(intentoId)
            .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));
        
        if (intento.isCompletado() || intento.isExpirado()) {
            throw new IllegalStateException("No se puede modificar un intento completado o expirado");
        }
        
        Pregunta pregunta = preguntaRepository.findById(preguntaId)
            .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));
        
        // Buscar respuesta existente o crear nueva
        RespuestaEstudiante respuesta = respuestaEstudianteRepository
            .findByIntentoIdAndPreguntaId(intentoId, preguntaId)
            .orElse(new RespuestaEstudiante(intento, pregunta));
        
        respuesta.setRespuestaTexto(respuestaTexto);
        respuesta.setOpcionSeleccionadaId(opcionSeleccionadaId);
        respuesta.setOpcionesOrdenadas(opcionesOrdenadas);
        respuesta.setFechaRespuesta(LocalDateTime.now());
        
        // Calificar automáticamente si es posible
        calificarRespuestaAutomaticamente(respuesta, pregunta);
        
        return respuestaEstudianteRepository.save(respuesta);
    }

    public List<RespuestaEstudiante> getRespuestasByIntentoId(Long intentoId) {
        return respuestaEstudianteRepository.findByIntentoId(intentoId);
    }

    // ===========================
    // CALIFICACIÓN AUTOMÁTICA
    // ===========================
    @Transactional
    private void calificarRespuestaAutomaticamente(RespuestaEstudiante respuesta, Pregunta pregunta) {
        if (pregunta.getTipo() == TipoPregunta.OPCION_MULTIPLE || 
            pregunta.getTipo() == TipoPregunta.VERDADERO_FALSO) {
            
            if (respuesta.getOpcionSeleccionadaId() != null) {
                OpcionRespuesta opcionSeleccionada = opcionRespuestaRepository
                    .findById(respuesta.getOpcionSeleccionadaId())
                    .orElse(null);
                
                if (opcionSeleccionada != null) {
                    respuesta.setEsCorrecta(opcionSeleccionada.getEsCorrecta());
                    respuesta.setPuntosObtenidos(opcionSeleccionada.getEsCorrecta() ? pregunta.getPuntos() : 0);
                }
            }
        } else if (pregunta.getTipo() == TipoPregunta.ORDENAR) {
            // Verificar orden correcto
            List<OpcionRespuesta> opcionesCorrectas = opcionRespuestaRepository
                .findByPreguntaIdOrderByOrden(pregunta.getId());
            
            if (respuesta.getOpcionesOrdenadas() != null && !opcionesCorrectas.isEmpty()) {
                // Comparar orden (simplificado - se puede mejorar)
                boolean ordenCorrecto = verificarOrdenCorrecto(respuesta.getOpcionesOrdenadas(), opcionesCorrectas);
                respuesta.setEsCorrecta(ordenCorrecto);
                respuesta.setPuntosObtenidos(ordenCorrecto ? pregunta.getPuntos() : 0);
            }
        }
        // DESARROLLO y COMPLETAR requieren calificación manual
    }

    private boolean verificarOrdenCorrecto(String opcionesOrdenadas, List<OpcionRespuesta> opcionesCorrectas) {
        try {
            // Parsear JSON array de IDs
            String[] ids = opcionesOrdenadas.replace("[", "").replace("]", "").replace("\"", "").split(",");
            if (ids.length != opcionesCorrectas.size()) return false;
            
            for (int i = 0; i < ids.length; i++) {
                Long id = Long.parseLong(ids[i].trim());
                if (!opcionesCorrectas.get(i).getId().equals(id)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    private void calificarIntentoAutomaticamente(Long intentoId) {
        IntentoEvaluacion intento = intentoEvaluacionRepository.findById(intentoId)
            .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));
        
        List<RespuestaEstudiante> respuestas = respuestaEstudianteRepository.findByIntentoId(intentoId);
        
        int puntosTotales = 0;
        int puntosMaximos = intento.getEvaluacion().getPuntosMaximos();
        
        for (RespuestaEstudiante respuesta : respuestas) {
            if (respuesta.getPuntosObtenidos() != null) {
                puntosTotales += respuesta.getPuntosObtenidos();
            }
        }
        
        // Calcular calificación (0-100)
        double calificacion = puntosMaximos > 0 ? (puntosTotales * 100.0) / puntosMaximos : 0;
        
        // Crear o actualizar calificación
        Calificacion cal = calificacionRepository.findByIntentoId(intentoId)
            .orElse(new Calificacion(calificacion, intento, true));
        
        cal.setCalificacion(calificacion);
        cal.setCalificacionAutomatica(true);
        calificacionRepository.save(cal);
        
        intento.setEstado(EstadoIntento.CALIFICADO);
        intentoEvaluacionRepository.save(intento);
    }

    // ===========================
    // CALIFICACIÓN MANUAL
    // ===========================
    @Transactional
    public Calificacion calificarIntentoManual(Long intentoId, Double calificacion, String comentarios) {
        IntentoEvaluacion intento = intentoEvaluacionRepository.findById(intentoId)
            .orElseThrow(() -> new IllegalArgumentException("Intento no encontrado"));
        
        Calificacion cal = calificacionRepository.findByIntentoId(intentoId)
            .orElse(new Calificacion(calificacion, intento, false));
        
        cal.setCalificacion(calificacion);
        cal.setComentarios(comentarios);
        cal.setCalificacionAutomatica(false);
        
        Calificacion saved = calificacionRepository.save(cal);
        intento.setEstado(EstadoIntento.CALIFICADO);
        intentoEvaluacionRepository.save(intento);
        
        return saved;
    }

    @Transactional
    public RespuestaEstudiante calificarRespuestaManual(Long respuestaId, Integer puntosObtenidos, String retroalimentacion) {
        RespuestaEstudiante respuesta = respuestaEstudianteRepository.findById(respuestaId)
            .orElseThrow(() -> new IllegalArgumentException("Respuesta no encontrada"));
        
        respuesta.setPuntosObtenidos(puntosObtenidos);
        respuesta.setRetroalimentacion(retroalimentacion);
        
        return respuestaEstudianteRepository.save(respuesta);
    }

    // ===========================
    // VERIFICACIÓN DE TIEMPO
    // ===========================
    @Transactional
    public void verificarIntentosExpirados() {
        List<IntentoEvaluacion> intentosEnProgreso = intentoEvaluacionRepository.findAll()
            .stream()
            .filter(i -> i.getEstado() == EstadoIntento.EN_PROGRESO)
            .filter(IntentoEvaluacion::isExpirado)
            .toList();
        
        for (IntentoEvaluacion intento : intentosEnProgreso) {
            finalizarIntento(intento.getId());
        }
    }
}

