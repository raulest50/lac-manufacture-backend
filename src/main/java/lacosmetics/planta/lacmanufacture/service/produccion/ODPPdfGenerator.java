package lacosmetics.planta.lacmanufacture.service.produccion;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lacosmetics.planta.lacmanufacture.config.StorageProperties;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.produccion.PlanificacionProduccion;
import lacosmetics.planta.lacmanufacture.model.produccion.RecursoAsignadoOrden;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ODPPdfGenerator {

    private final OrdenProduccionRepo ordenProduccionRepo;
    private final StorageProperties storageProperties;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final BaseColor PRIMARY_COLOR = new BaseColor(231, 38, 141);
    private static final BaseColor SECONDARY_COLOR = new BaseColor(242, 220, 219);
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, PRIMARY_COLOR);
    private static final Font SECTION_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
    private static final Font LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font VALUE_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font SMALL_VALUE_FONT = new Font(Font.FontFamily.HELVETICA, 9);

    public byte[] generateOrdenProduccionPdf(int ordenId) {
        Optional<OrdenProduccion> ordenOptional = ordenProduccionRepo.findByIdWithDetalles(ordenId);
        OrdenProduccion orden = ordenOptional.orElseThrow(
                () -> new IllegalArgumentException("Orden de producción no encontrada con id: " + ordenId)
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document);
            addTitle(document);
            addResumenOrden(document, orden);
            addCronograma(document, orden);
            addDatosComerciales(document, orden);
            addBloquePlanificacion(document, orden.getPlanificacionProduccion());
            addTablaInsumos(document, orden.getOrdenesSeguimiento());
            addTablaRecursos(document, orden.getOrdenesSeguimiento());

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error("Error generando PDF para orden de producción {}", ordenId, e);
            throw new RuntimeException("No fue posible generar el PDF de la orden de producción " + ordenId, e);
        }
    }

    private void addHeader(Document document) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(new float[]{1.2f, 2f});
        headerTable.setWidthPercentage(100);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        Image logo = loadCompanyLogo();
        if (logo != null) {
            logo.scaleToFit(120, 70);
            logoCell.addElement(logo);
        }
        headerTable.addCell(logoCell);

        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoCell.setPaddingTop(5);
        infoCell.addElement(createCompanyInfoParagraph());
        headerTable.addCell(infoCell);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);
    }

    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("ORDEN DE PRODUCCIÓN", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(12f);
        document.add(title);
    }

    private void addResumenOrden(Document document, OrdenProduccion orden) throws DocumentException {
        addSectionTitle(document, "Resumen de la orden");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(createLabelCell("ID Orden"));
        table.addCell(createValueCell(String.valueOf(orden.getOrdenId())));

        Producto producto = orden.getProducto();
        String productoTexto = producto != null ?
                String.format("%s (%s)", safeText(producto.getNombre()), safeText(producto.getProductoId())) : "-";
        table.addCell(createLabelCell("Producto"));
        table.addCell(createValueCell(productoTexto));

        table.addCell(createLabelCell("Número de lotes"));
        table.addCell(createValueCell(String.valueOf(orden.getNumeroLotes())));

        table.addCell(createLabelCell("Estado"));
        table.addCell(createValueCell(mapEstadoOrden(orden.getEstadoOrden())));

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addCronograma(Document document, OrdenProduccion orden) throws DocumentException {
        addSectionTitle(document, "Cronograma");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(createLabelCell("Fecha de creación"));
        table.addCell(createValueCell(formatDateTime(orden.getFechaCreacion())));

        table.addCell(createLabelCell("Fecha de lanzamiento"));
        table.addCell(createValueCell(formatDateTime(orden.getFechaLanzamiento())));

        table.addCell(createLabelCell("Inicio real"));
        table.addCell(createValueCell(formatDateTime(orden.getFechaInicio())));

        table.addCell(createLabelCell("Final planificada"));
        table.addCell(createValueCell(formatDateTime(orden.getFechaFinalPlanificada())));

        table.addCell(createLabelCell("Final real"));
        table.addCell(createValueCell(formatDateTime(orden.getFechaFinal())));

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addDatosComerciales(Document document, OrdenProduccion orden) throws DocumentException {
        addSectionTitle(document, "Datos comerciales");
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(createLabelCell("Pedido comercial"));
        table.addCell(createValueCell(safeText(orden.getNumeroPedidoComercial())));

        table.addCell(createLabelCell("Área operativa"));
        table.addCell(createValueCell(safeText(orden.getAreaOperativa())));

        table.addCell(createLabelCell("Departamento operativo"));
        table.addCell(createValueCell(safeText(orden.getDepartamentoOperativo())));

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addBloquePlanificacion(Document document, PlanificacionProduccion planificacion) throws DocumentException {
        addSectionTitle(document, "Planificación");
        if (planificacion == null) {
            Paragraph sinPlanificacion = new Paragraph("Sin planificación asociada", VALUE_FONT);
            sinPlanificacion.setAlignment(Element.ALIGN_LEFT);
            sinPlanificacion.setSpacingAfter(8f);
            document.add(sinPlanificacion);
            return;
        }

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(createLabelCell("Cantidad a producir"));
        table.addCell(createValueCell(formatCantidad(planificacion.getCantidadProducir())));

        table.addCell(createLabelCell("Fecha planificada"));
        table.addCell(createValueCell(formatDateTime(planificacion.getFechaPlanificada())));

        table.addCell(createLabelCell("Prioridad"));
        table.addCell(createValueCell(mapPrioridad(planificacion.getPrioridad())));

        table.addCell(createLabelCell("Estado"));
        table.addCell(createValueCell(mapEstadoPlanificacion(planificacion.getEstado())));

        List<String> recursos = Optional.ofNullable(planificacion.getRecursosAsignados())
                .orElse(List.of())
                .stream()
                .map(recurso -> safeText(recurso.getNombre()))
                .filter(nombre -> !nombre.isBlank())
                .sorted()
                .collect(Collectors.toList());

        table.addCell(createLabelCell("Recursos planificados"));
        table.addCell(createValueCell(recursos.isEmpty() ? "-" : String.join(", ", recursos)));

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addTablaInsumos(Document document, List<OrdenSeguimiento> seguimientos) throws DocumentException {
        addSectionTitle(document, "Insumos y seguimiento");
        if (seguimientos == null || seguimientos.isEmpty()) {
            Paragraph sinInsumos = new Paragraph("Sin insumos registrados", VALUE_FONT);
            sinInsumos.setSpacingAfter(8f);
            document.add(sinInsumos);
            return;
        }

        PdfPTable table = new PdfPTable(new float[]{1.1f, 2.5f, 1.1f, 1.2f, 1.5f, 1.5f});
        table.setWidthPercentage(100);

        addTableHeaderCell(table, "Código");
        addTableHeaderCell(table, "Insumo / Producto");
        addTableHeaderCell(table, "Cantidad");
        addTableHeaderCell(table, "Estado");
        addTableHeaderCell(table, "Inicio");
        addTableHeaderCell(table, "Final");

        seguimientos.stream()
                .sorted(Comparator.comparingInt(OrdenSeguimiento::getSeguimientoId))
                .forEach(seguimiento -> {
                    Insumo insumo = seguimiento.getInsumo();
                    Producto insumoProducto = insumo != null ? insumo.getProducto() : null;

                    table.addCell(createBodyCell(insumo != null ? String.valueOf(insumo.getInsumoId()) : "-"));

                    String descripcionInsumo;
                    if (insumoProducto != null) {
                        descripcionInsumo = String.format(
                                "%s (%s)",
                                safeText(insumoProducto.getNombre()),
                                safeText(insumoProducto.getProductoId())
                        );
                    } else if (insumo != null) {
                        descripcionInsumo = "Insumo " + insumo.getInsumoId();
                    } else {
                        descripcionInsumo = "-";
                    }
                    table.addCell(createBodyCell(descripcionInsumo));

                    table.addCell(createBodyCell(formatCantidad(insumo != null ? insumo.getCantidadRequerida() : null)));
                    table.addCell(createBodyCell(mapEstadoSeguimiento(seguimiento.getEstado())));
                    table.addCell(createBodyCell(formatDateTime(seguimiento.getFechaInicio())));
                    table.addCell(createBodyCell(formatDateTime(seguimiento.getFechaFinalizacion())));
                });

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addTablaRecursos(Document document, List<OrdenSeguimiento> seguimientos) throws DocumentException {
        addSectionTitle(document, "Recursos asignados");
        List<RecursoAsignadoOrden> recursos = Optional.ofNullable(seguimientos)
                .orElse(List.of())
                .stream()
                .flatMap(seguimiento -> Optional.ofNullable(seguimiento.getRecursosAsignados()).orElse(List.of()).stream())
                .sorted(Comparator.comparing(recurso -> Optional.ofNullable(recurso.getRecursoProduccion())
                        .map(lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion::getNombre)
                        .orElse("")))
                .collect(Collectors.toList());

        if (recursos.isEmpty()) {
            Paragraph sinRecursos = new Paragraph("Sin recursos asignados", VALUE_FONT);
            sinRecursos.setSpacingAfter(8f);
            document.add(sinRecursos);
            return;
        }

        PdfPTable table = new PdfPTable(new float[]{2.2f, 2.2f, 2.0f, 1.3f, 1.3f, 1.2f});
        table.setWidthPercentage(100);

        addTableHeaderCell(table, "Recurso");
        addTableHeaderCell(table, "Activo asociado");
        addTableHeaderCell(table, "Ventana temporal");
        addTableHeaderCell(table, "Setup (min)");
        addTableHeaderCell(table, "Proceso (min)");
        addTableHeaderCell(table, "Estado");

        for (RecursoAsignadoOrden recurso : recursos) {
            table.addCell(createBodyCell(formatRecurso(recurso)));
            table.addCell(createBodyCell(formatActivo(recurso.getActivoFijoAsignado())));
            table.addCell(createBodyCell(formatVentanaTemporal(recurso.getFechaInicio(), recurso.getFechaFin())));
            table.addCell(createBodyCell(formatTiempo(recurso.getTiempoSetupReal())));
            table.addCell(createBodyCell(formatTiempo(recurso.getTiempoProcesoReal())));
            table.addCell(createBodyCell(mapEstadoRecurso(recurso.getEstado())));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph sectionTitle = new Paragraph(title.toUpperCase(Locale.ROOT), SECTION_TITLE_FONT);
        sectionTitle.setSpacingBefore(6f);
        sectionTitle.setSpacingAfter(6f);
        document.add(sectionTitle);
    }

    private Paragraph createCompanyInfoParagraph() {
        Paragraph companyInfo = new Paragraph();
        companyInfo.setAlignment(Element.ALIGN_RIGHT);
        Font infoFont = new Font(Font.FontFamily.HELVETICA, 9);
        companyInfo.add(new Chunk("Napolitana J.P S.A.S.\n", infoFont));
        companyInfo.add(new Chunk("NIT: 901751897-1\n", infoFont));
        companyInfo.add(new Chunk("Tel: 301 711 51 81\n", infoFont));
        companyInfo.add(new Chunk("jorgerafaelpereiraosorio1@gmail.com", infoFont));
        return companyInfo;
    }

    private Image loadCompanyLogo() {
        try {
            Path basePath = Paths.get(storageProperties.getUPLOAD_DIR(), "assets/logos");
            List<String> candidates = List.of("logo_exotic.png", "logo_exotic.jpg", "logo_exotic.svg");
            for (String candidate : candidates) {
                Path logoPath = basePath.resolve(candidate);
                if (Files.exists(logoPath)) {
                    return Image.getInstance(logoPath.toAbsolutePath().toString());
                }
            }
        } catch (IOException | BadElementException e) {
            log.warn("No se pudo cargar el logo corporativo", e);
        }
        return null;
    }

    private PdfPCell createLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, LABEL_FONT));
        cell.setBackgroundColor(SECONDARY_COLOR);
        cell.setPadding(6f);
        cell.setBorderColor(SECONDARY_COLOR.darker());
        return cell;
    }

    private PdfPCell createValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, VALUE_FONT));
        cell.setPadding(6f);
        cell.setBorderColor(new BaseColor(230, 230, 230));
        return cell;
    }

    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell headerCell = new PdfPCell(new Phrase(text.toUpperCase(Locale.ROOT), LABEL_FONT));
        headerCell.setBackgroundColor(SECONDARY_COLOR);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(6f);
        headerCell.setBorderColor(SECONDARY_COLOR.darker());
        table.addCell(headerCell);
    }

    private PdfPCell createBodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL_VALUE_FONT));
        cell.setPadding(6f);
        cell.setBorderColor(new BaseColor(235, 235, 235));
        return cell;
    }

    private String mapEstadoOrden(int estado) {
        return switch (estado) {
            case 0 -> "En producción";
            case 1 -> "Terminada";
            case 2 -> "Cancelada";
            default -> "-";
        };
    }

    private String mapEstadoSeguimiento(int estado) {
        return switch (estado) {
            case 0 -> "Pendiente";
            case 1 -> "Finalizado";
            case 2 -> "En proceso";
            default -> "-";
        };
    }

    private String mapEstadoRecurso(Integer estado) {
        if (estado == null) {
            return "-";
        }
        return switch (estado) {
            case 0 -> "Asignado";
            case 1 -> "En uso";
            case 2 -> "Finalizado";
            default -> "-";
        };
    }

    private String mapEstadoPlanificacion(Integer estado) {
        if (estado == null) {
            return "-";
        }
        return switch (estado) {
            case 0 -> "Tentativa";
            case 1 -> "Confirmada";
            case 2 -> "En ejecución";
            case 3 -> "Completada";
            case 4 -> "Cancelada";
            default -> "-";
        };
    }

    private String mapPrioridad(Integer prioridad) {
        if (prioridad == null) {
            return "-";
        }
        return switch (prioridad) {
            case 1 -> "Alta";
            case 2 -> "Media";
            case 3 -> "Baja";
            default -> "-";
        };
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    private String formatTiempo(Double minutos) {
        if (minutos == null) {
            return "-";
        }
        return String.format(Locale.ROOT, "%.1f", minutos);
    }

    private String formatCantidad(Double cantidad) {
        if (cantidad == null) {
            return "-";
        }
        return String.format(Locale.ROOT, "%.2f", cantidad);
    }

    private String formatRecurso(RecursoAsignadoOrden recurso) {
        if (recurso == null || recurso.getRecursoProduccion() == null) {
            return "-";
        }
        String nombre = safeText(recurso.getRecursoProduccion().getNombre());
        String descripcion = safeText(recurso.getRecursoProduccion().getDescripcion());
        if (!descripcion.equals("-")) {
            return nombre + " - " + descripcion;
        }
        return nombre;
    }

    private String formatActivo(ActivoFijo activo) {
        if (activo == null) {
            return "-";
        }
        String nombre = safeText(activo.getNombre());
        String id = safeText(activo.getId());
        return nombre.equals("-") ? id : nombre + " (" + id + ")";
    }

    private String formatVentanaTemporal(LocalDateTime inicio, LocalDateTime fin) {
        String inicioTexto = formatDateTime(inicio);
        String finTexto = formatDateTime(fin);
        if (inicioTexto.equals("-") && finTexto.equals("-")) {
            return "-";
        }
        if (finTexto.equals("-")) {
            return inicioTexto;
        }
        if (inicioTexto.equals("-")) {
            return finTexto;
        }
        return inicioTexto + " a " + finTexto;
    }

    private String safeText(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}
