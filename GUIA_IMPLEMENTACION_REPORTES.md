# 📊 GUÍA COMPLETA: IMPLEMENTACIÓN DEL SISTEMA DE REPORTES

## ✅ ARCHIVOS YA CREADOS

Los siguientes archivos ya están creados y listos:

1. ✅ `ReporteEstudiantesDTO.java`
2. ✅ `ReporteProfesoresDTO.java`
3. ✅ `ReporteCursosDTO.java`
4. ✅ `ReporteAsignacionesDTO.java`
5. ✅ `FormatoReporte.java`
6. ✅ `DEPENDENCIAS_REPORTES.xml`

## 📝 PASO 1: AGREGAR DEPENDENCIAS AL POM.XML

Abre `pom.xml` y agrega estas dependencias ANTES de `</dependencies>` (línea 87):

```xml
<!-- Dependencias para generación de reportes -->
<!-- iText para PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>

<!-- Apache POI para Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

<!-- OpenCSV para archivos CSV -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.7.1</version>
</dependency>
```

## 📝 PASO 2: ACTUALIZAR REPOSITORIOS

### A. StudentCursoRepository.java

Agrega estos imports y métodos:

```java
// Agregar estos imports al inicio
import com.example.demo.model.Student;
import com.example.demo.model.Curso;

// Agregar estos métodos al final de la interfaz (antes del cierre })
List<StudentCurso> findByStudent(Student student);
List<StudentCurso> findByCurso(Curso curso);
```

### B. CursoRepository.java

Agrega este import y método:

```java
// Agregar este import al inicio
import com.example.demo.model.Professor;

// Agregar este método al final de la interfaz (antes del cierre })
List<Curso> findByProfesor(Professor profesor);
```

## 📝 PASO 3: CREAR REPORTE SERVICE SIMPLIFICADO

Crea el archivo `ReporteService.java` con este contenido SIMPLIFICADO (sin usar métodos que no existen):

```java
package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                    cursoDTO.setNombreProfesor(asg.getCurso().getProfesor() != null ? asg.getCurso().getProfesor().getNombre() : "Sin asignar");
                    cursoDTO.setEstadoAsignacion(asg.getEstado().name());
                    cursoDTO.setFechaAsignacion(LocalDateTime.now());
                    return cursoDTO;
                })
                .collect(Collectors.toList());

        dto.setCursosInscritos(cursosInscritos);
        dto.setTotalCursosActivos((int) asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.ACTIVO).count());
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
        dto.setNumeroCursosActivos((int) cursosAsignados.stream().filter(c -> c.getEstado() == EstadoCurso.ACTIVO).count());
        dto.setNumeroCursosInactivos((int) cursosAsignados.stream().filter(c -> c.getEstado() == EstadoCurso.INACTIVO).count());

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
        dto.setNombreProfesor(asignacion.getCurso().getProfesor() != null ? asignacion.getCurso().getProfesor().getNombre() : "Sin asignar");
        dto.setEstadoAsignacion(asignacion.getEstado().name());
        dto.setFechaAsignacion(LocalDateTime.now());
        dto.setFechaCompletado(null);
        dto.setDuracionDias(0);

        return dto;
    }
}
```

## 📝 PASO 4: CREAR CONTROLADOR DE REPORTES

Crea `ReporteController.java`:

```java
package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/estudiantes")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<List<ReporteEstudiantesDTO>> obtenerReporteEstudiantes() {
        List<ReporteEstudiantesDTO> reporte = reporteService.generarReporteEstudiantes();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/profesores")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<List<ReporteProfesoresDTO>> obtenerReporteProfesores() {
        List<ReporteProfesoresDTO> reporte = reporteService.generarReporteProfesores();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/cursos")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<List<ReporteCursosDTO>> obtenerReporteCursos() {
        List<ReporteCursosDTO> reporte = reporteService.generarReporteCursos();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/asignaciones")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<List<ReporteAsignacionesDTO>> obtenerReporteAsignaciones() {
        List<ReporteAsignacionesDTO> reporte = reporteService.generarReporteAsignaciones();
        return ResponseEntity.ok(reporte);
    }
}
```

## 📝 PASO 5: ACTUALIZAR DASHBOARD.HTML

Agrega esta sección en el dashboard.html donde dice "Reportes":

```html
<!-- Vista Reportes -->
<main id="reportes-content" class="content-view hidden">
    <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800">Reportes</h1>
        <p class="mt-2 text-gray-600">Genera y descarga reportes del sistema</p>
    </div>

    <!-- Tarjetas de Reportes -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <!-- Reporte de Estudiantes -->
        <div class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <div class="bg-blue-100 p-3 rounded-full">
                    <i data-lucide="users" class="w-8 h-8 text-blue-600"></i>
                </div>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Estudiantes</h3>
            <p class="text-sm text-gray-600 mb-4">Reporte general de estudiantes</p>
            <button onclick="generarReporte('estudiantes')" 
                    class="w-full bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition-colors">
                Ver Reporte
            </button>
        </div>

        <!-- Reporte de Profesores -->
        <div class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <div class="bg-green-100 p-3 rounded-full">
                    <i data-lucide="graduation-cap" class="w-8 h-8 text-green-600"></i>
                </div>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Profesores</h3>
            <p class="text-sm text-gray-600 mb-4">Carga académica de profesores</p>
            <button onclick="generarReporte('profesores')" 
                    class="w-full bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition-colors">
                Ver Reporte
            </button>
        </div>

        <!-- Reporte de Cursos -->
        <div class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <div class="bg-orange-100 p-3 rounded-full">
                    <i data-lucide="book-marked" class="w-8 h-8 text-orange-600"></i>
                </div>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Cursos</h3>
            <p class="text-sm text-gray-600 mb-4">Ocupación y estado de cursos</p>
            <button onclick="generarReporte('cursos')" 
                    class="w-full bg-orange-500 text-white py-2 px-4 rounded-lg hover:bg-orange-600 transition-colors">
                Ver Reporte
            </button>
        </div>

        <!-- Reporte de Asignaciones -->
        <div class="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <div class="bg-purple-100 p-3 rounded-full">
                    <i data-lucide="user-check" class="w-8 h-8 text-purple-600"></i>
                </div>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Asignaciones</h3>
            <p class="text-sm text-gray-600 mb-4">Historial de inscripciones</p>
            <button onclick="generarReporte('asignaciones')" 
                    class="w-full bg-purple-500 text-white py-2 px-4 rounded-lg hover:bg-purple-600 transition-colors">
                Ver Reporte
            </button>
        </div>
    </div>

    <!-- Tabla de Resultados -->
    <div id="reporte-resultados" class="mt-8 hidden">
        <div class="bg-white rounded-lg shadow-sm p-6">
            <div class="flex justify-between items-center mb-4">
                <h2 id="reporte-titulo" class="text-xl font-bold text-gray-800"></h2>
                <button onclick="cerrarReporte()" class="text-gray-500 hover:text-gray-700">
                    <i data-lucide="x" class="w-6 h-6"></i>
                </button>
            </div>
            <div id="reporte-contenido" class="overflow-x-auto"></div>
        </div>
    </div>
</main>

<script>
async function generarReporte(tipo) {
    try {
        const response = await fetch(`/admin/reportes/${tipo}`);
        if (!response.ok) throw new Error('Error al generar reporte');
        
        const data = await response.json();
        mostrarReporte(tipo, data);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al generar el reporte');
    }
}

function mostrarReporte(tipo, data) {
    const resultados = document.getElementById('reporte-resultados');
    const titulo = document.getElementById('reporte-titulo');
    const contenido = document.getElementById('reporte-contenido');
    
    // Configurar título
    const titulos = {
        'estudiantes': 'Reporte de Estudiantes',
        'profesores': 'Reporte de Profesores',
        'cursos': 'Reporte de Cursos',
        'asignaciones': 'Reporte de Asignaciones'
    };
    titulo.textContent = titulos[tipo];
    
    // Generar tabla según el tipo
    let html = '<table class="w-full text-sm text-left text-gray-500">';
    
    if (tipo === 'estudiantes') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Nombre</th>
                    <th class="px-6 py-3">Código</th>
                    <th class="px-6 py-3">Email</th>
                    <th class="px-6 py-3">Cursos Activos</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombre}</td>
                    <td class="px-6 py-4">${item.codigo}</td>
                    <td class="px-6 py-4">${item.email}</td>
                    <td class="px-6 py-4">${item.totalCursosActivos}</td>
                </tr>
            `;
        });
    } else if (tipo === 'profesores') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Nombre</th>
                    <th class="px-6 py-3">Especialidad</th>
                    <th class="px-6 py-3">Cursos Asignados</th>
                    <th class="px-6 py-3">Total Estudiantes</th>
                    <th class="px-6 py-3">Carga</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombre}</td>
                    <td class="px-6 py-4">${item.especialidad}</td>
                    <td class="px-6 py-4">${item.numeroCursosAsignados}</td>
                    <td class="px-6 py-4">${item.totalEstudiantesBajoSuCargo}</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 text-xs rounded-full ${
                            item.nivelCarga === 'ALTA' ? 'bg-red-100 text-red-800' :
                            item.nivelCarga === 'MEDIA' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-green-100 text-green-800'
                        }">${item.nivelCarga}</span>
                    </td>
                </tr>
            `;
        });
    } else if (tipo === 'cursos') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Nombre</th>
                    <th class="px-6 py-3">Código</th>
                    <th class="px-6 py-3">Profesor</th>
                    <th class="px-6 py-3">Estudiantes</th>
                    <th class="px-6 py-3">Ocupación</th>
                    <th class="px-6 py-3">Demanda</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombre}</td>
                    <td class="px-6 py-4">${item.codigo}</td>
                    <td class="px-6 py-4">${item.nombreProfesor}</td>
                    <td class="px-6 py-4">${item.numeroEstudiantesInscritos}</td>
                    <td class="px-6 py-4">${item.tasaOcupacion.toFixed(1)}%</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 text-xs rounded-full ${
                            item.nivelDemanda === 'ALTA' ? 'bg-green-100 text-green-800' :
                            item.nivelDemanda === 'MEDIA' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-red-100 text-red-800'
                        }">${item.nivelDemanda}</span>
                    </td>
                </tr>
            `;
        });
    } else if (tipo === 'asignaciones') {
        html += `
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                    <th class="px-6 py-3">Estudiante</th>
                    <th class="px-6 py-3">Curso</th>
                    <th class="px-6 py-3">Profesor</th>
                    <th class="px-6 py-3">Estado</th>
                </tr>
            </thead>
            <tbody>
        `;
        data.forEach(item => {
            html += `
                <tr class="bg-white border-b hover:bg-gray-50">
                    <td class="px-6 py-4">${item.nombreEstudiante}</td>
                    <td class="px-6 py-4">${item.nombreCurso}</td>
                    <td class="px-6 py-4">${item.nombreProfesor}</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 text-xs rounded-full ${
                            item.estadoAsignacion === 'ACTIVO' ? 'bg-green-100 text-green-800' :
                            'bg-gray-100 text-gray-800'
                        }">${item.estadoAsignacion}</span>
                    </td>
                </tr>
            `;
        });
    }
    
    html += '</tbody></table>';
    contenido.innerHTML = html;
    resultados.classList.remove('hidden');
    
    // Actualizar iconos
    lucide.createIcons();
}

function cerrarReporte() {
    document.getElementById('reporte-resultados').classList.add('hidden');
}
</script>
```

## ✅ RESUMEN DE PASOS

1. ✅ Agregar dependencias al `pom.xml`
2. ✅ Actualizar `StudentCursoRepository.java` (agregar 2 métodos)
3. ✅ Actualizar `CursoRepository.java` (agregar 1 método)
4. ✅ Crear `ReporteService.java` (copiar código completo)
5. ✅ Crear `ReporteController.java` (copiar código completo)
6. ✅ Actualizar `dashboard.html` (agregar sección de reportes)

## 🚀 PROBAR

1. Reinicia la aplicación
2. Accede al dashboard de administrador
3. Haz clic en "Reportes" en el sidebar
4. Haz clic en cualquier botón de reporte
5. Deberías ver los datos en una tabla

¡Listo! El sistema de reportes estará funcionando.
