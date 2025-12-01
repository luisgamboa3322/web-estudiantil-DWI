package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ReporteService;
import com.example.demo.service.PdfExportService;
import com.example.demo.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ExcelExportService excelExportService;

    // ==================== ENDPOINTS DE VISUALIZACIÓN ====================

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

    // ==================== ENDPOINTS DE EXPORTACIÓN PDF ====================

    @GetMapping("/estudiantes/pdf")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarEstudiantesPDF() {
        List<ReporteEstudiantesDTO> reporte = reporteService.generarReporteEstudiantes();
        byte[] pdfBytes = pdfExportService.exportarEstudiantesPDF(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("estudiantes", "pdf"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/profesores/pdf")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarProfesoresPDF() {
        List<ReporteProfesoresDTO> reporte = reporteService.generarReporteProfesores();
        byte[] pdfBytes = pdfExportService.exportarProfesoresPDF(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("profesores", "pdf"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/cursos/pdf")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarCursosPDF() {
        List<ReporteCursosDTO> reporte = reporteService.generarReporteCursos();
        byte[] pdfBytes = pdfExportService.exportarCursosPDF(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("cursos", "pdf"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/asignaciones/pdf")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarAsignacionesPDF() {
        List<ReporteAsignacionesDTO> reporte = reporteService.generarReporteAsignaciones();
        byte[] pdfBytes = pdfExportService.exportarAsignacionesPDF(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("asignaciones", "pdf"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // ==================== ENDPOINTS DE EXPORTACIÓN EXCEL ====================

    @GetMapping("/estudiantes/excel")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarEstudiantesExcel() {
        List<ReporteEstudiantesDTO> reporte = reporteService.generarReporteEstudiantes();
        byte[] excelBytes = excelExportService.exportarEstudiantesExcel(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("estudiantes", "xlsx"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/profesores/excel")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarProfesoresExcel() {
        List<ReporteProfesoresDTO> reporte = reporteService.generarReporteProfesores();
        byte[] excelBytes = excelExportService.exportarProfesoresExcel(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("profesores", "xlsx"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/cursos/excel")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarCursosExcel() {
        List<ReporteCursosDTO> reporte = reporteService.generarReporteCursos();
        byte[] excelBytes = excelExportService.exportarCursosExcel(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("cursos", "xlsx"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/asignaciones/excel")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_DASHBOARD')")
    public ResponseEntity<byte[]> descargarAsignacionesExcel() {
        List<ReporteAsignacionesDTO> reporte = reporteService.generarReporteAsignaciones();
        byte[] excelBytes = excelExportService.exportarAsignacionesExcel(reporte);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", generarNombreArchivo("asignaciones", "xlsx"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    // ==================== MÉTODO AUXILIAR ====================

    private String generarNombreArchivo(String tipo, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("reporte_%s_%s.%s", tipo, timestamp, extension);
    }
}