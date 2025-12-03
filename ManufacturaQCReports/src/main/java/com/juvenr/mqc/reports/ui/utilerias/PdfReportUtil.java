package com.juvenr.mqc.reports.ui.utileriasutil;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;

public class PdfReportUtil {

    // Constantes de configuración
    private static final float MARGIN = 40;
    private static final float TOP_MARGIN = 60;
    private static final float BOTTOM_MARGIN = 50;
    private static final float ROW_HEIGHT = 22;
    private static final float HEADER_HEIGHT = 28;
    private static final float CELL_PADDING = 6;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont BODY_FONT = PDType1Font.HELVETICA;
    private static final PDFont TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final float TITLE_FONT_SIZE = 18;
    private static final float HEADER_FONT_SIZE = 11;
    private static final float BODY_FONT_SIZE = 10;
    private static final Color HEADER_BG_COLOR = new Color(41, 128, 185);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Color ODD_ROW_COLOR = new Color(249, 249, 249);
    private static final Color EVEN_ROW_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(221, 221, 221);
    
    // Colores para severidad
    private static final Color CRITICA_COLOR = new Color(231, 76, 60);    // Rojo
    private static final Color ALTA_COLOR = new Color(230, 126, 34);      // Naranja
    private static final Color MEDIA_COLOR = new Color(241, 196, 15);     // Amarillo
    private static final Color BAJA_COLOR = new Color(39, 174, 96);       // Verde
    
    // Formateadores
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter ISO_DATE_PARSER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final NumberFormat MXN_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    private static final NumberFormat USD_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);

    static {
        // Configurar el formateador de MXN para mostrar "$" en lugar de "MXN"
        if (MXN_FORMATTER instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) MXN_FORMATTER;
            DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
            symbols.setCurrencySymbol("$");
            df.setDecimalFormatSymbols(symbols);
        }
    }

    /**
     * Exporta el contenido de un JTable a un PDF con formato profesional
     */
    public static void exportTableToPdf(JTable table, File destFile) throws IOException {
        exportTableToPdf(table, destFile, null, null);
    }

    /**
     * Exporta el contenido de un JTable a un PDF con título y subtítulo personalizados
     */
    public static void exportTableToPdf(JTable table, File destFile, String title, String subtitle) throws IOException {
        TableModel model = table.getModel();
        int columnCount = model.getColumnCount();
        int rowCount = model.getRowCount();

        try (PDDocument document = new PDDocument()) {
            // Calcular anchos de columnas proporcionales
            float[] columnWidths = calculateColumnWidths(model, document);
            float totalTableWidth = sum(columnWidths);
            
            // Crear primera página
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float tableStartX = (pageWidth - totalTableWidth) / 2; // Centrar tabla
            
            float currentY = pageHeight - TOP_MARGIN;
            int currentPage = 1;
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true)) {
                // Dibujar encabezado del reporte
                currentY = drawReportHeader(contentStream, document, page, title, subtitle, currentY);
                
                // Dibujar información de resumen
                currentY = drawSummaryInfo(contentStream, pageWidth, currentY, rowCount, columnCount);
                
                // Dibujar encabezado de la tabla
                currentY = drawTableHeader(contentStream, model, tableStartX, currentY, columnWidths);
                
                // Dibujar filas de datos con paginación
                currentY = drawTableRows(contentStream, document, page, model, tableStartX, 
                                        currentY, columnWidths, rowCount, currentPage);
                
                // Dibujar pie de página
                drawFooter(contentStream, document, page, currentPage);
                
            } catch (Exception e) {
                throw new IOException("Error al generar PDF: " + e.getMessage(), e);
            }
            
            document.save(destFile);
        }
    }

    /**
     * Formatea un valor de celda según el tipo de columna
     */
    private static String formatCellValue(Object value, String columnName) {
        if (value == null) return "";
        
        String columnNameLower = columnName.toLowerCase();
        
        // Formatear fechas ISO a formato legible
        if (columnNameLower.contains("fecha") || columnNameLower.contains("timestamp")) {
            return formatDate(value.toString());
        }
        
        // Formatear montos monetarios
        if (columnNameLower.contains("costo") || columnNameLower.contains("monto") || 
            columnNameLower.contains("precio") || columnNameLower.contains("importe")) {
            return formatCurrency(value, columnName);
        }
        
        // Formatear números con separadores de miles
        if (isNumeric(value)) {
            return formatNumber(value);
        }
        
        return value.toString();
    }
    
    /**
     * Formatea fecha ISO a formato legible
     */
    private static String formatDate(String isoDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDate, ISO_DATE_PARSER);
            return dateTime.format(DATE_TIME_FORMATTER);
        } catch (Exception e) {
            // Si falla el parseo, devolver el valor original
            return isoDate;
        }
    }
    
    /**
     * Formatea valores monetarios
     */
    private static String formatCurrency(Object value, String columnName) {
        try {
            double amount = Double.parseDouble(value.toString());
            
            if (columnName.toUpperCase().contains("USD")) {
                return USD_FORMATTER.format(amount);
            } else if (columnName.toUpperCase().contains("MXN") || 
                      columnName.toUpperCase().contains("PESO") ||
                      columnName.toUpperCase().contains("MEX")) {
                return MXN_FORMATTER.format(amount);
            } else {
                // Por defecto, usar formato MXN
                return MXN_FORMATTER.format(amount);
            }
        } catch (NumberFormatException e) {
            return value.toString();
        }
    }
    
    /**
     * Formatea números con separadores de miles
     */
    private static String formatNumber(Object value) {
        try {
            double number = Double.parseDouble(value.toString());
            if (number == Math.floor(number)) {
                // Es entero
                return String.format("%,.0f", number);
            } else {
                // Es decimal
                return String.format("%,.2f", number);
            }
        } catch (NumberFormatException e) {
            return value.toString();
        }
    }
    
    /**
     * Verifica si un valor es numérico
     */
    private static boolean isNumeric(Object value) {
        if (value == null) return false;
        
        try {
            Double.parseDouble(value.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Obtiene el color según la severidad
     */
    private static Color getSeverityColor(String severity) {
        if (severity == null) return Color.BLACK;
        
        switch (severity.toUpperCase()) {
            case "CRITICA":
            case "CRÍTICA":
            case "CRITICAL":
                return CRITICA_COLOR;
            case "ALTA":
            case "HIGH":
                return ALTA_COLOR;
            case "MEDIA":
            case "MEDIUM":
                return MEDIA_COLOR;
            case "BAJA":
            case "LOW":
                return BAJA_COLOR;
            default:
                return Color.BLACK;
        }
    }

    /**
     * Calcula anchos de columnas proporcionales basados en el contenido
     */
    private static float[] calculateColumnWidths(TableModel model, PDDocument document) throws IOException {
        int columnCount = model.getColumnCount();
        float[] widths = new float[columnCount];
        float maxWidthPerColumn = 120; // Ancho máximo por columna
        float minWidthPerColumn = 60;  // Ancho mínimo por columna
        
        // Calcular anchos basados en encabezados y contenido
        for (int col = 0; col < columnCount; col++) {
            // Considerar ancho del encabezado
            String header = model.getColumnName(col);
            float headerWidth = HEADER_FONT.getStringWidth(header) / 1000 * HEADER_FONT_SIZE;
            
            // Considerar ancho del contenido más largo en la columna
            float maxContentWidth = 0;
            for (int row = 0; row < Math.min(model.getRowCount(), 50); row++) { // Muestrear primeras 50 filas
                Object value = model.getValueAt(row, col);
                if (value != null) {
                    String text = formatCellValue(value, model.getColumnName(col));
                    float textWidth = BODY_FONT.getStringWidth(text) / 1000 * BODY_FONT_SIZE;
                    maxContentWidth = Math.max(maxContentWidth, textWidth);
                }
            }
            
            // Usar el mayor ancho entre encabezado y contenido, con límites
            float calculatedWidth = Math.max(headerWidth, maxContentWidth) + (CELL_PADDING * 2);
            widths[col] = Math.max(minWidthPerColumn, Math.min(maxWidthPerColumn, calculatedWidth));
        }
        
        return widths;
    }

    /**
     * Dibuja el encabezado del reporte con título, fecha y logo
     */
    private static float drawReportHeader(PDPageContentStream contentStream, PDDocument document, 
                                         PDPage page, String title, String subtitle, float y) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        
        // Título principal
        String reportTitle = title != null ? title : "Reporte de Calidad";
        String reportSubtitle = subtitle != null ? subtitle : "ManufacturaQC";
        
        contentStream.setFont(TITLE_FONT, TITLE_FONT_SIZE);
        contentStream.setNonStrokingColor(HEADER_BG_COLOR);
        
        float titleWidth = TITLE_FONT.getStringWidth(reportTitle) / 1000 * TITLE_FONT_SIZE;
        float titleX = (pageWidth - titleWidth) / 2;
        
        contentStream.beginText();
        contentStream.newLineAtOffset(titleX, y);
        contentStream.showText(reportTitle);
        contentStream.endText();
        
        y -= 25;
        
        // Subtítulo
        contentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE - 2);
        contentStream.setNonStrokingColor(Color.GRAY);
        
        float subtitleWidth = HEADER_FONT.getStringWidth(reportSubtitle) / 1000 * (HEADER_FONT_SIZE - 2);
        float subtitleX = (pageWidth - subtitleWidth) / 2;
        
        contentStream.beginText();
        contentStream.newLineAtOffset(subtitleX, y);
        contentStream.showText(reportSubtitle);
        contentStream.endText();
        
        y -= 20;
        
        // Línea separadora
        contentStream.setStrokingColor(HEADER_BG_COLOR);
        contentStream.setLineWidth(1);
        contentStream.moveTo(MARGIN, y);
        contentStream.lineTo(pageWidth - MARGIN, y);
        contentStream.stroke();
        
        y -= 20;
        
        // Fecha de generación
        String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        contentStream.setFont(BODY_FONT, BODY_FONT_SIZE - 1);
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        
        contentStream.beginText();
        contentStream.newLineAtOffset(pageWidth - MARGIN - 150, y + 40); // Posición superior derecha
        contentStream.showText("Generado: " + dateTime);
        contentStream.endText();
        
        return y;
    }

    /**
     * Dibuja información resumida del reporte
     */
    private static float drawSummaryInfo(PDPageContentStream contentStream, float pageWidth, 
                                        float y, int rowCount, int columnCount) throws IOException {
        contentStream.setFont(BODY_FONT, BODY_FONT_SIZE);
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        
        String summary = String.format("Total de registros: %d | Columnas: %d", rowCount, columnCount);
        
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(summary);
        contentStream.endText();
        
        return y - 30;
    }

    /**
     * Dibuja el encabezado de la tabla
     */
    private static float drawTableHeader(PDPageContentStream contentStream, TableModel model, 
                                        float startX, float y, float[] columnWidths) throws IOException {
        float currentX = startX;
        
        // Fondo del encabezado
        contentStream.setNonStrokingColor(HEADER_BG_COLOR);
        contentStream.addRect(startX, y - HEADER_HEIGHT, sum(columnWidths), HEADER_HEIGHT);
        contentStream.fill();
        
        // Texto de encabezados
        contentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
        contentStream.setNonStrokingColor(HEADER_TEXT_COLOR);
        
        for (int col = 0; col < model.getColumnCount(); col++) {
            String header = model.getColumnName(col);
            float textWidth = HEADER_FONT.getStringWidth(header) / 1000 * HEADER_FONT_SIZE;
            float textX = currentX + (columnWidths[col] - textWidth) / 2; // Centrar texto
            
            contentStream.beginText();
            contentStream.newLineAtOffset(textX, y - HEADER_HEIGHT + CELL_PADDING);
            contentStream.showText(header);
            contentStream.endText();
            
            // Bordes de columnas
            contentStream.setStrokingColor(Color.WHITE);
            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(currentX + columnWidths[col], y);
            contentStream.lineTo(currentX + columnWidths[col], y - HEADER_HEIGHT);
            contentStream.stroke();
            
            currentX += columnWidths[col];
        }
        
        // Bordes superior e inferior del encabezado
        contentStream.setStrokingColor(Color.WHITE);
        contentStream.setLineWidth(1);
        contentStream.moveTo(startX, y);
        contentStream.lineTo(startX + sum(columnWidths), y);
        contentStream.moveTo(startX, y - HEADER_HEIGHT);
        contentStream.lineTo(startX + sum(columnWidths), y - HEADER_HEIGHT);
        contentStream.stroke();
        
        return y - HEADER_HEIGHT;
    }

    /**
     * Dibuja las filas de la tabla con paginación automática
     */
    private static float drawTableRows(PDPageContentStream contentStream, PDDocument document, 
                                      PDPage page, TableModel model, float startX, float y,
                                      float[] columnWidths, int rowCount, int currentPage) throws IOException {
        float pageHeight = page.getMediaBox().getHeight();
        float currentY = y;
        
        for (int row = 0; row < rowCount; row++) {
            // Verificar si necesitamos nueva página
            if (currentY < BOTTOM_MARGIN + ROW_HEIGHT) {
                contentStream.close();
                
                // Crear nueva página
                PDPage newPage = new PDPage(PDRectangle.LETTER);
                document.addPage(newPage);
                currentPage++;
                
                contentStream = new PDPageContentStream(document, newPage, AppendMode.APPEND, true);
                currentY = pageHeight - TOP_MARGIN;
                
                // Dibujar encabezado continuo en páginas siguientes
                currentY = drawContinuedHeader(contentStream, newPage, currentY, currentPage);
            }
            
            // Color de fondo alternado para filas
            if (row % 2 == 0) {
                contentStream.setNonStrokingColor(EVEN_ROW_COLOR);
            } else {
                contentStream.setNonStrokingColor(ODD_ROW_COLOR);
            }
            
            contentStream.addRect(startX, currentY - ROW_HEIGHT, sum(columnWidths), ROW_HEIGHT);
            contentStream.fill();
            
            float cellX = startX;
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                String text = formatCellValue(value, model.getColumnName(col));
                
                // Truncar texto si es muy largo
                if (text.length() > 30) {
                    text = text.substring(0, 27) + "...";
                }
                
                // Configurar color según severidad si es columna de severidad
                String columnName = model.getColumnName(col).toLowerCase();
                if (columnName.contains("severidad") || columnName.contains("prioridad") || 
                    columnName.contains("nivel")) {
                    Color severityColor = getSeverityColor(text);
                    contentStream.setNonStrokingColor(severityColor);
                } else {
                    contentStream.setNonStrokingColor(Color.BLACK);
                }
                
                contentStream.setFont(BODY_FONT, BODY_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(cellX + CELL_PADDING, currentY - ROW_HEIGHT + CELL_PADDING);
                contentStream.showText(text);
                contentStream.endText();
                
                // Bordes de celda
                contentStream.setStrokingColor(BORDER_COLOR);
                contentStream.setLineWidth(0.3f);
                contentStream.moveTo(cellX + columnWidths[col], currentY);
                contentStream.lineTo(cellX + columnWidths[col], currentY - ROW_HEIGHT);
                contentStream.stroke();
                
                cellX += columnWidths[col];
            }
            
            // Borde inferior de fila
            contentStream.setStrokingColor(BORDER_COLOR);
            contentStream.setLineWidth(0.3f);
            contentStream.moveTo(startX, currentY - ROW_HEIGHT);
            contentStream.lineTo(startX + sum(columnWidths), currentY - ROW_HEIGHT);
            contentStream.stroke();
            
            currentY -= ROW_HEIGHT;
        }
        
        return currentY;
    }

    /**
     * Dibuja encabezado para páginas siguientes
     */
    private static float drawContinuedHeader(PDPageContentStream contentStream, PDPage page, 
                                            float y, int pageNumber) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        
        contentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
        contentStream.setNonStrokingColor(Color.GRAY);
        
        String continuationText = "Reporte de Calidad (Continuación)";
        float textWidth = HEADER_FONT.getStringWidth(continuationText) / 1000 * HEADER_FONT_SIZE;
        float textX = (pageWidth - textWidth) / 2;
        
        contentStream.beginText();
        contentStream.newLineAtOffset(textX, y);
        contentStream.showText(continuationText);
        contentStream.endText();
        
        y -= 25;
        
        // Línea separadora
        contentStream.setStrokingColor(Color.LIGHT_GRAY);
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN, y);
        contentStream.lineTo(pageWidth - MARGIN, y);
        contentStream.stroke();
        
        return y - 20;
    }

    /**
     * Dibuja el pie de página con número de página
     */
    private static void drawFooter(PDPageContentStream contentStream, PDDocument document, 
                                  PDPage page, int pageNumber) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        float footerY = BOTTOM_MARGIN - 20;
        
        // Línea separadora del pie
        contentStream.setStrokingColor(Color.LIGHT_GRAY);
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN, footerY + 15);
        contentStream.lineTo(pageWidth - MARGIN, footerY + 15);
        contentStream.stroke();
        
        // Número de página
        contentStream.setFont(BODY_FONT, BODY_FONT_SIZE - 1);
        contentStream.setNonStrokingColor(Color.GRAY);
        
        String pageText = String.format("Página %d", pageNumber);
        float textWidth = BODY_FONT.getStringWidth(pageText) / 1000 * (BODY_FONT_SIZE - 1);
        
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - textWidth) / 2, footerY);
        contentStream.showText(pageText);
        contentStream.endText();
        
        // Información de la empresa
        String companyInfo = "ManufacturaQC © " + new SimpleDateFormat("yyyy").format(new Date());
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, footerY);
        contentStream.showText(companyInfo);
        contentStream.endText();
    }

    /**
     * Suma los valores de un arreglo de floats
     */
    private static float sum(float[] array) {
        float total = 0;
        for (float value : array) {
            total += value;
        }
        return total;
    }

    /**
     * Método para exportar con opciones adicionales
     */
    public static void exportTableToPdf(JTable table, File destFile, ReportOptions options) throws IOException {
        if (options == null) {
            options = new ReportOptions();
        }
        exportTableToPdf(table, destFile, options.getTitle(), options.getSubtitle());
    }

    /**
     * Clase para opciones del reporte
     */
    public static class ReportOptions {
        private String title;
        private String subtitle;
        private boolean includeLogo;
        private boolean landscape;
        private boolean colorCodeSeverity = true;
        
        public ReportOptions() {
            this.title = "Reporte de Calidad";
            this.subtitle = "ManufacturaQC";
            this.includeLogo = false;
            this.landscape = false;
        }
        
        // Getters y Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getSubtitle() { return subtitle; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        
        public boolean isIncludeLogo() { return includeLogo; }
        public void setIncludeLogo(boolean includeLogo) { this.includeLogo = includeLogo; }
        
        public boolean isLandscape() { return landscape; }
        public void setLandscape(boolean landscape) { this.landscape = landscape; }
        
        public boolean isColorCodeSeverity() { return colorCodeSeverity; }
        public void setColorCodeSeverity(boolean colorCodeSeverity) { this.colorCodeSeverity = colorCodeSeverity; }
    }
    
    /**
     * Método de prueba principal
     */
    public static void main(String[] args) {
        System.out.println("=== EJEMPLO DE FORMATO ===");
        System.out.println();
        System.out.println("Formato de fecha:");
        System.out.println("ISO: 2025-12-03T14:29:04.536Z");
        System.out.println("Legible: " + formatDate("2025-12-03T14:29:04.536Z"));
        System.out.println();
        System.out.println("Formato de moneda:");
        System.out.println("MXN: " + formatCurrency("500.00", "Costo MXN"));
        System.out.println("USD: " + formatCurrency("24.39", "Costo USD"));
        System.out.println();
        System.out.println("Colores de severidad:");
        System.out.println("CRITICA: " + getSeverityColor("CRITICA"));
        System.out.println("ALTA: " + getSeverityColor("ALTA"));
        System.out.println("BAJA: " + getSeverityColor("BAJA"));
    }
}