package com.example.demo.enums;

/**
 * Enum para los formatos de exportación de reportes
 */
public enum FormatoReporte {
    PDF("application/pdf", ".pdf"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    CSV("text/csv", ".csv");

    private final String contentType;
    private final String extension;

    FormatoReporte(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }
}
