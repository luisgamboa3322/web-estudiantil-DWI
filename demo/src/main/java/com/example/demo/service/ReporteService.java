package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private StudentCursoRepository studentCursoRepository;

    /**
     * Genera el reporte general de estudiantes
     */
    public List<ReporteEstudiantesDTO> generarReporteEstudiantes() {
        List<Student> estudiantes = studentRepository.findAll();
        return estudiantes.stream()
                .map(this::convertirAReporteEstudiante)
                .collect(Collectors.toList());
    }

    /**
     * Genera el reporte de carga académica de profesores
     */
    public List<ReporteProfesoresDTO> generarReporteProfesores() {
        List<Professor> profesores = professorRepository.findAll();
        return profesores.stream()
                .map(this::convertirAReporteProfesor)
                .collect(Collectors.toList());
    }

    /**
     * Genera el reporte de ocupación de cursos
     */
    public List<ReporteCursosDTO> generarReporteCursos() {
        List<Curso> cursos = cursoRepository.findAll();
        return cursos.stream()
                .map(this::convertirAReporteCurso)
                .collect(Collectors.toList());
    }

    /**
     * Genera el reporte de asignaciones
     */
    public List<ReporteAsignacionesDTO> generarReporteAsignaciones() {
        List<StudentCurso> asignaciones = studentCursoRepository.findAll();
        return asignaciones.stream()
                .map(this::convertirAReporteAsignacion)
                .collect(Collectors.toList());
    }

    // ==================== MÉTODOS DE CONVERSIÓN ====================

    private ReporteEstudiantesDTO convertirAReporteEstudiante(Student student) {
        ReporteEstudiantesDTO dto = new ReporteEstudiantesDTO();
        dto.setId(student.getId());
        dto.setNombre(student.getNombre());
        dto.setCodigo(student.getCodigo());
        dto.setEmail(student.getEmail());
        dto.setEstado("ACTIVO");
        dto.setFechaRegistro(LocalDateTime.now()); // Simplificado

        // Obtener cursos inscritos
        List<StudentCurso> asignaciones = studentCursoRepository.findByStudent(student);
        List<ReporteEstudiantesDTO.CursoAsignadoDTO> cursosInscritos = asignaciones.stream()
                .map(asg -> {
                    ReporteEstudiantesDTO.CursoAsignadoDTO cursoDTO = new ReporteEstudiantesDTO.CursoAsignadoDTO();
                    cursoDTO.setNombreCurso(asg.getCurso().getNombre());
                    cursoDTO.setCodigoCurso(asg.getCurso().getCodigo());
                    cursoDTO.setNombreProfesor(
                            asg.getCurso().getProfesor() != null ? asg.getCurso().getProfesor().getNombre()
                                    : "Sin asignar");
                    cursoDTO.setEstadoAsignacion(asg.getEstado().name());
                    cursoDTO.setFechaAsignacion(LocalDateTime.now());
                    return cursoDTO;
                })
                .collect(Collectors.toList());

        dto.setCursosInscritos(cursosInscritos);
        dto.setTotalCursosActivos(
                (int) asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.ACTIVO).count());
        dto.setTotalCursosCompletados(0); // Simplificado

        return dto;
    }

    private ReporteProfesoresDTO convertirAReporteProfesor(Professor profesor) {
        ReporteProfesoresDTO dto = new ReporteProfesoresDTO();
        dto.setId(profesor.getId());
        dto.setNombre(profesor.getNombre());
        dto.setCodigo(profesor.getCodigo());
        dto.setEmail(profesor.getEmail());
        dto.setEspecialidad(profesor.getEspecialidad());

        // Obtener cursos asignados
        List<Curso> cursosAsignados = cursoRepository.findByProfesor(profesor);
        dto.setNumeroCursosAsignados(cursosAsignados.size());
        dto.setNumeroCursosActivos(
                (int) cursosAsignados.stream().filter(c -> c.getEstado() == EstadoCurso.ACTIVO).count());
        dto.setNumeroCursosInactivos(
                (int) cursosAsignados.stream().filter(c -> c.getEstado() == EstadoCurso.INACTIVO).count());

        // Calcular total de estudiantes
        int totalEstudiantes = cursosAsignados.stream()
                .mapToInt(curso -> studentCursoRepository.findByCurso(curso).size())
                .sum();
        dto.setTotalEstudiantesBajoSuCargo(totalEstudiantes);

        // Convertir cursos a DTO
        List<ReporteProfesoresDTO.CursoProfesorDTO> cursosDTO = cursosAsignados.stream()
                .map(curso -> {
                    ReporteProfesoresDTO.CursoProfesorDTO cursoDTO = new ReporteProfesoresDTO.CursoProfesorDTO();
                    cursoDTO.setNombreCurso(curso.getNombre());
                    cursoDTO.setCodigoCurso(curso.getCodigo());
                    cursoDTO.setEstadoCurso(curso.getEstado().name());
                    cursoDTO.setNumeroEstudiantes(studentCursoRepository.findByCurso(curso).size());
                    return cursoDTO;
                })
                .collect(Collectors.toList());
        dto.setCursosAsignados(cursosDTO);

        // Determinar nivel de carga
        if (cursosAsignados.size() <= 2) {
            dto.setNivelCarga("LIGERA");
        } else if (cursosAsignados.size() <= 4) {
            dto.setNivelCarga("MEDIA");
        } else {
            dto.setNivelCarga("ALTA");
        }

        return dto;
    }

    private ReporteCursosDTO convertirAReporteCurso(Curso curso) {
        ReporteCursosDTO dto = new ReporteCursosDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setCodigo(curso.getCodigo());
        dto.setDescripcion(curso.getDescripcion());
        dto.setEstado(curso.getEstado().name());
        dto.setNombreProfesor(curso.getProfesor() != null ? curso.getProfesor().getNombre() : "Sin asignar");
        dto.setEspecialidadProfesor(curso.getProfesor() != null ? curso.getProfesor().getEspecialidad() : "N/A");

        // Calcular ocupación
        int numeroEstudiantes = studentCursoRepository.findByCurso(curso).size();
        dto.setNumeroEstudiantesInscritos(numeroEstudiantes);
        dto.setCapacidadMaxima(30); // Valor por defecto

        double tasaOcupacion = (numeroEstudiantes * 100.0) / 30;
        dto.setTasaOcupacion(tasaOcupacion);

        // Determinar nivel de demanda
        if (tasaOcupacion >= 80) {
            dto.setNivelDemanda("ALTA");
        } else if (tasaOcupacion >= 50) {
            dto.setNivelDemanda("MEDIA");
        } else {
            dto.setNivelDemanda("BAJA");
        }

        return dto;
    }

    private ReporteAsignacionesDTO convertirAReporteAsignacion(StudentCurso asignacion) {
        ReporteAsignacionesDTO dto = new ReporteAsignacionesDTO();
        dto.setId(asignacion.getId());
        dto.setNombreEstudiante(asignacion.getStudent().getNombre());
        dto.setCodigoEstudiante(asignacion.getStudent().getCodigo());
        dto.setNombreCurso(asignacion.getCurso().getNombre());
        dto.setCodigoCurso(asignacion.getCurso().getCodigo());
        dto.setNombreProfesor(
                asignacion.getCurso().getProfesor() != null ? asignacion.getCurso().getProfesor().getNombre()
                        : "Sin asignar");
        dto.setEstadoAsignacion(asignacion.getEstado().name());
        dto.setFechaAsignacion(LocalDateTime.now());
        dto.setFechaCompletado(null);
        dto.setDuracionDias(0);

        return dto;
    }
}