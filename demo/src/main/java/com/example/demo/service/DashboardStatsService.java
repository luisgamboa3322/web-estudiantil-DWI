package com.example.demo.service;

import com.example.demo.dto.ActividadRecienteDTO;
import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.dto.InscripcionTendenciaDTO;
import com.example.demo.model.EstadoAsignacion;
import com.example.demo.model.EstadoCurso;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardStatsService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final StudentCursoRepository studentCursoRepository;

    public DashboardStatsService(StudentRepository studentRepository,
            ProfessorRepository professorRepository,
            CursoRepository cursoRepository,
            StudentCursoRepository studentCursoRepository) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.cursoRepository = cursoRepository;
        this.studentCursoRepository = studentCursoRepository;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Métricas clave
        stats.setTotalEstudiantesActivos(studentRepository.count());
        stats.setTotalProfesores(professorRepository.count());
        stats.setTotalCursosActivos(cursoRepository.countByEstado(EstadoCurso.ACTIVO));
        stats.setTotalAsignacionesActivas(studentCursoRepository.countByEstado(EstadoAsignacion.ACTIVO));

        // Calcular tasa de ocupación y promedio
        long totalCursos = cursoRepository.count();
        long cursosConEstudiantes = studentCursoRepository.findAll().stream()
                .map(sc -> sc.getCurso().getId())
                .distinct()
                .count();

        stats.setTasaOcupacionCursos(totalCursos > 0 ? (cursosConEstudiantes * 100.0 / totalCursos) : 0.0);

        long totalAsignaciones = studentCursoRepository.count();
        stats.setPromedioEstudiantesPorCurso(totalCursos > 0 ? (totalAsignaciones * 1.0 / totalCursos) : 0.0);

        // Estudiantes por curso (top 10)
        Map<String, Long> estudiantesPorCurso = studentCursoRepository.findAll().stream()
                .filter(sc -> sc.getEstado() == EstadoAsignacion.ACTIVO)
                .collect(Collectors.groupingBy(
                        sc -> sc.getCurso().getNombre(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        stats.setEstudiantesPorCurso(estudiantesPorCurso);

        // Cursos por estado
        Map<String, Long> cursosPorEstado = new LinkedHashMap<>();
        cursosPorEstado.put("ACTIVO", cursoRepository.countByEstado(EstadoCurso.ACTIVO));
        cursosPorEstado.put("INACTIVO", cursoRepository.countByEstado(EstadoCurso.INACTIVO));
        stats.setCursosPorEstado(cursosPorEstado);

        // Tendencia de inscripciones (últimos 7 días)
        List<InscripcionTendenciaDTO> tendencia = getTendenciaInscripciones();
        stats.setTendenciaInscripciones(tendencia);

        // Actividad reciente
        List<ActividadRecienteDTO> actividades = getActividadReciente();
        stats.setActividadReciente(actividades);

        return stats;
    }

    private List<InscripcionTendenciaDTO> getTendenciaInscripciones() {
        List<InscripcionTendenciaDTO> tendencia = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        // Obtener todas las asignaciones
        var asignaciones = studentCursoRepository.findAll();

        // Agrupar por fecha (últimos 7 días)
        LocalDate hoy = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            long count = asignaciones.stream()
                    .filter(a -> a.getFechaAsignacion() != null &&
                            a.getFechaAsignacion().toLocalDate().equals(fecha))
                    .count();
            tendencia.add(new InscripcionTendenciaDTO(fecha.format(formatter), count));
        }

        return tendencia;
    }

    private List<ActividadRecienteDTO> getActividadReciente() {
        List<ActividadRecienteDTO> actividades = new ArrayList<>();

        // Últimos estudiantes registrados (top 5)
        var estudiantes = studentRepository.findAll().stream()
                .sorted((s1, s2) -> {
                    if (s1.getId() == null)
                        return 1;
                    if (s2.getId() == null)
                        return -1;
                    return s2.getId().compareTo(s1.getId());
                })
                .limit(5)
                .collect(Collectors.toList());

        for (var estudiante : estudiantes) {
            actividades.add(new ActividadRecienteDTO(
                    "ESTUDIANTE",
                    "Nuevo estudiante: " + estudiante.getNombre(),
                    LocalDateTime.now().minusHours(estudiantes.indexOf(estudiante)),
                    "user-plus"));
        }

        // Últimas asignaciones (top 5)
        var asignaciones = studentCursoRepository.findAll().stream()
                .filter(a -> a.getFechaAsignacion() != null)
                .sorted((a1, a2) -> a2.getFechaAsignacion().compareTo(a1.getFechaAsignacion()))
                .limit(5)
                .collect(Collectors.toList());

        for (var asignacion : asignaciones) {
            actividades.add(new ActividadRecienteDTO(
                    "ASIGNACION",
                    asignacion.getStudent().getNombre() + " inscrito en " + asignacion.getCurso().getNombre(),
                    asignacion.getFechaAsignacion(),
                    "book-open"));
        }

        // Últimos cursos creados (top 3)
        var cursos = cursoRepository.findAll().stream()
                .sorted((c1, c2) -> {
                    if (c1.getId() == null)
                        return 1;
                    if (c2.getId() == null)
                        return -1;
                    return c2.getId().compareTo(c1.getId());
                })
                .limit(3)
                .collect(Collectors.toList());

        for (var curso : cursos) {
            actividades.add(new ActividadRecienteDTO(
                    "CURSO",
                    "Nuevo curso: " + curso.getNombre(),
                    LocalDateTime.now().minusDays(cursos.indexOf(curso)),
                    "book-marked"));
        }

        // Ordenar por fecha descendente
        actividades.sort((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()));

        // Retornar solo las 10 más recientes
        return actividades.stream().limit(10).collect(Collectors.toList());
    }
}
