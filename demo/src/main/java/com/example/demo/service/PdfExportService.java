package com.example.demo.service;

import com.example.demo.dto.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para exportar reportes a PDF usando iText 7
 */
@Service
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta reporte de estudiantes a PDF
     */
    public byte[] exportarEstudiantesPDF(List<ReporteEstudiantesDTO> estudiantes) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("REPORTE DE ESTUDIANTES")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha de generación
            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(fecha);

            document.add(new Paragraph("\n"));

            // Tabla
            float[] columnWidths = { 3, 2, 4, 2 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addHeaderCell(createHeaderCell("Nombre"));
            table.addHeaderCell(createHeaderCell("Código"));
            table.addHeaderCell(createHeaderCell("Email"));
            table.addHeaderCell(createHeaderCell("Cursos Activos"));

            // Datos
            for (ReporteEstudiantesDTO estudiante : estudiantes) {
                table.addCell(new Cell().add(new Paragraph(estudiante.getNombre())));
                table.addCell(new Cell().add(new Paragraph(estudiante.getCodigo())));
                table.addCell(new Cell().add(new Paragraph(estudiante.getEmail())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(estudiante.getTotalCursosActivos()))));
            }

            document.add(table);

            // Total
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total de estudiantes: " + estudiantes.size())
                    .setBold());

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de estudiantes", e);
        }
    }

    /**
     * Exporta reporte de profesores a PDF
     */
    public byte[] exportarProfesoresPDF(List<ReporteProfesoresDTO> profesores) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("REPORTE DE PROFESORES - CARGA ACADÉMICA")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha de generación
            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(fecha);

            document.add(new Paragraph("\n"));

            // Tabla
            float[] columnWidths = { 3, 2, 2, 2, 2 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addHeaderCell(createHeaderCell("Nombre"));
            table.addHeaderCell(createHeaderCell("Especialidad"));
            table.addHeaderCell(createHeaderCell("Cursos"));
            table.addHeaderCell(createHeaderCell("Estudiantes"));
            table.addHeaderCell(createHeaderCell("Carga"));

            // Datos
            for (ReporteProfesoresDTO profesor : profesores) {
                table.addCell(new Cell().add(new Paragraph(profesor.getNombre())));
                table.addCell(new Cell().add(new Paragraph(profesor.getEspecialidad())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(profesor.getNumeroCursosAsignados()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(profesor.getTotalEstudiantesBajoSuCargo()))));
                table.addCell(new Cell().add(new Paragraph(profesor.getNivelCarga())));
            }

            document.add(table);

            // Total
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total de profesores: " + profesores.size())
                    .setBold());

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de profesores", e);
        }
    }

    /**
     * Exporta reporte de cursos a PDF
     */
    public byte[] exportarCursosPDF(List<ReporteCursosDTO> cursos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("REPORTE DE CURSOS - OCUPACIÓN")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha de generación
            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(fecha);

            document.add(new Paragraph("\n"));

            // Tabla
            float[] columnWidths = { 3, 2, 3, 2, 2, 2 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addHeaderCell(createHeaderCell("Nombre"));
            table.addHeaderCell(createHeaderCell("Código"));
            table.addHeaderCell(createHeaderCell("Profesor"));
            table.addHeaderCell(createHeaderCell("Estudiantes"));
            table.addHeaderCell(createHeaderCell("Ocupación"));
            table.addHeaderCell(createHeaderCell("Demanda"));

            // Datos
            for (ReporteCursosDTO curso : cursos) {
                table.addCell(new Cell().add(new Paragraph(curso.getNombre())));
                table.addCell(new Cell().add(new Paragraph(curso.getCodigo())));
                table.addCell(new Cell().add(new Paragraph(curso.getNombreProfesor())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(curso.getNumeroEstudiantesInscritos()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", curso.getTasaOcupacion()))));
                table.addCell(new Cell().add(new Paragraph(curso.getNivelDemanda())));
            }

            document.add(table);

            // Total
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total de cursos: " + cursos.size())
                    .setBold());

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de cursos", e);
        }
    }

    /**
     * Exporta reporte de asignaciones a PDF
     */
    public byte[] exportarAsignacionesPDF(List<ReporteAsignacionesDTO> asignaciones) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("REPORTE DE ASIGNACIONES")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Fecha de generación
            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(fecha);

            document.add(new Paragraph("\n"));

            // Tabla
            float[] columnWidths = { 3, 3, 3, 2 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addHeaderCell(createHeaderCell("Estudiante"));
            table.addHeaderCell(createHeaderCell("Curso"));
            table.addHeaderCell(createHeaderCell("Profesor"));
            table.addHeaderCell(createHeaderCell("Estado"));

            // Datos
            for (ReporteAsignacionesDTO asignacion : asignaciones) {
                table.addCell(new Cell().add(new Paragraph(asignacion.getNombreEstudiante())));
                table.addCell(new Cell().add(new Paragraph(asignacion.getNombreCurso())));
                table.addCell(new Cell().add(new Paragraph(asignacion.getNombreProfesor())));
                table.addCell(new Cell().add(new Paragraph(asignacion.getEstadoAsignacion())));
            }

            document.add(table);

            // Total
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total de asignaciones: " + asignaciones.size())
                    .setBold());

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de asignaciones", e);
        }
    }

    /**
     * Crea una celda de encabezado con estilo
     */
    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
    }
}
