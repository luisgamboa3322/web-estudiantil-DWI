package com.example.demo.service;

import com.example.demo.dto.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para exportar reportes a Excel usando Apache POI
 */
@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta reporte de estudiantes a Excel
     */
    public byte[] exportarEstudiantesExcel(List<ReporteEstudiantesDTO> estudiantes) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Estudiantes");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE ESTUDIANTES");
            titleCell.setCellStyle(createTitleStyle(workbook));

            // Fecha
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado: " + LocalDateTime.now().format(DATE_FORMATTER));

            // Espacio
            sheet.createRow(2);

            // Encabezados
            Row headerRow = sheet.createRow(3);
            String[] headers = { "Nombre", "Código", "Email", "Cursos Activos", "Cursos Completados" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 4;
            for (ReporteEstudiantesDTO estudiante : estudiantes) {
                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(estudiante.getNombre());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(estudiante.getCodigo());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(estudiante.getEmail());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(estudiante.getTotalCursosActivos());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(estudiante.getTotalCursosCompletados());
                cell4.setCellStyle(dataStyle);
            }

            // Total
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalCell = totalRow.createCell(0);
            totalCell.setCellValue("Total de estudiantes: " + estudiantes.size());
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            totalCell.setCellStyle(boldStyle);

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de estudiantes", e);
        }
    }

    /**
     * Exporta reporte de profesores a Excel
     */
    public byte[] exportarProfesoresExcel(List<ReporteProfesoresDTO> profesores) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Profesores");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE PROFESORES - CARGA ACADÉMICA");
            titleCell.setCellStyle(createTitleStyle(workbook));

            // Fecha
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado: " + LocalDateTime.now().format(DATE_FORMATTER));

            // Espacio
            sheet.createRow(2);

            // Encabezados
            Row headerRow = sheet.createRow(3);
            String[] headers = { "Nombre", "Especialidad", "Cursos Asignados", "Total Estudiantes", "Nivel de Carga" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 4;
            for (ReporteProfesoresDTO profesor : profesores) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(profesor.getNombre());
                row.createCell(1).setCellValue(profesor.getEspecialidad());
                row.createCell(2).setCellValue(profesor.getNumeroCursosAsignados());
                row.createCell(3).setCellValue(profesor.getTotalEstudiantesBajoSuCargo());
                row.createCell(4).setCellValue(profesor.getNivelCarga());

                for (int i = 0; i < 5; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Total
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalCell = totalRow.createCell(0);
            totalCell.setCellValue("Total de profesores: " + profesores.size());
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            totalCell.setCellStyle(boldStyle);

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de profesores", e);
        }
    }

    /**
     * Exporta reporte de cursos a Excel
     */
    public byte[] exportarCursosExcel(List<ReporteCursosDTO> cursos) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Cursos");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE CURSOS - OCUPACIÓN");
            titleCell.setCellStyle(createTitleStyle(workbook));

            // Fecha
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado: " + LocalDateTime.now().format(DATE_FORMATTER));

            // Espacio
            sheet.createRow(2);

            // Encabezados
            Row headerRow = sheet.createRow(3);
            String[] headers = { "Nombre", "Código", "Profesor", "Estudiantes", "Ocupación (%)", "Nivel Demanda" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 4;
            for (ReporteCursosDTO curso : cursos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(curso.getNombre());
                row.createCell(1).setCellValue(curso.getCodigo());
                row.createCell(2).setCellValue(curso.getNombreProfesor());
                row.createCell(3).setCellValue(curso.getNumeroEstudiantesInscritos());
                row.createCell(4).setCellValue(String.format("%.1f", curso.getTasaOcupacion()));
                row.createCell(5).setCellValue(curso.getNivelDemanda());

                for (int i = 0; i < 6; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Total
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalCell = totalRow.createCell(0);
            totalCell.setCellValue("Total de cursos: " + cursos.size());
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            totalCell.setCellStyle(boldStyle);

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de cursos", e);
        }
    }

    /**
     * Exporta reporte de asignaciones a Excel
     */
    public byte[] exportarAsignacionesExcel(List<ReporteAsignacionesDTO> asignaciones) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Asignaciones");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE ASIGNACIONES");
            titleCell.setCellStyle(createTitleStyle(workbook));

            // Fecha
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado: " + LocalDateTime.now().format(DATE_FORMATTER));

            // Espacio
            sheet.createRow(2);

            // Encabezados
            Row headerRow = sheet.createRow(3);
            String[] headers = { "Estudiante", "Código Estudiante", "Curso", "Profesor", "Estado" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 4;
            for (ReporteAsignacionesDTO asignacion : asignaciones) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(asignacion.getNombreEstudiante());
                row.createCell(1).setCellValue(asignacion.getCodigoEstudiante());
                row.createCell(2).setCellValue(asignacion.getNombreCurso());
                row.createCell(3).setCellValue(asignacion.getNombreProfesor());
                row.createCell(4).setCellValue(asignacion.getEstadoAsignacion());

                for (int i = 0; i < 5; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Total
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalCell = totalRow.createCell(0);
            totalCell.setCellValue("Total de asignaciones: " + asignaciones.size());
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            totalCell.setCellStyle(boldStyle);

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de asignaciones", e);
        }
    }

    // ==================== MÉTODOS DE ESTILO ====================

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }
}
