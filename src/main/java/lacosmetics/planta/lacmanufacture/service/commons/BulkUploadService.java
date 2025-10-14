package lacosmetics.planta.lacmanufacture.service.commons;

import lacosmetics.planta.lacmanufacture.config.StorageProperties;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload.BulkUploadResponseDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.Lote;
import lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload.MaterialBulkUploadMappingDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.repo.compras.FacturaCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.LoteRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenSeguimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.InsumoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Service for handling bulk uploads of suppliers and products.
 * This service provides methods for processing CSV or Excel files containing
 * multiple suppliers or products to be processed in batch.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BulkUploadService {

    private final TransaccionAlmacenRepo transaccionAlmacenRepo;
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;
    private final OrdenCompraRepo ordenCompraRepo;
    private final FacturaCompraRepo facturaCompraRepo;
    private final ProveedorRepo proveedorRepo;
    private final StorageProperties storageProperties;

    // Repositorios adicionales para productos
    private final ProductoRepo productoRepo;
    private final MaterialRepo materialRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;
    private final InsumoRepo insumoRepo;
    private final OrdenProduccionRepo ordenProduccionRepo;
    private final OrdenSeguimientoRepo ordenSeguimientoRepo;
    private final LoteRepo loteRepo;

    /**
     * Limpia todos los datos relacionados con proveedores para permitir una carga masiva.
     * Elimina registros en el siguiente orden para mantener la integridad referencial:
     * 1. Transacciones de almacén y sus movimientos
     * 2. Órdenes de compra y facturas (materiales)
     * 3. Proveedores
     * 4. Archivos en la carpeta de proveedores
     */
    @Transactional
    public void cleanAllProveedoresData() {
        log.info("Iniciando limpieza de datos de proveedores para carga masiva");

        try {
            // Nivel 1: Eliminar transacciones de almacén y sus movimientos
            log.info("Eliminando transacciones de almacén y movimientos");
            transaccionAlmacenRepo.deleteAll(); // Elimina los movimientos
            transaccionAlmacenHeaderRepo.deleteAll(); // Elimina las cabeceras de transacción

            // Nivel 2: Eliminar documentos principales que referencian a proveedores
            log.info("Eliminando órdenes de compra y facturas");
            ordenCompraRepo.deleteAll();
            facturaCompraRepo.deleteAll();

            // Nivel 3: Finalmente eliminar los proveedores
            log.info("Eliminando proveedores");
            proveedorRepo.deleteAll();

            // Eliminar archivos de proveedores
            deleteProveedoresFiles();

            log.info("Limpieza de datos de proveedores completada con éxito");
        } catch (Exception e) {
            log.error("Error durante la limpieza de datos de proveedores", e);
            throw new RuntimeException("Error al limpiar datos de proveedores: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina todos los archivos y carpetas en el directorio de proveedores.
     */
    private void deleteProveedoresFiles() {
        try {
            String baseDir = storageProperties.getUPLOAD_DIR();
            String proveedoresDir = storageProperties.getPROVEEDORES();
            Path proveedoresFolderPath = Paths.get(baseDir, proveedoresDir);

            if (Files.exists(proveedoresFolderPath)) {
                log.info("Eliminando archivos en la carpeta de proveedores: {}", proveedoresFolderPath);

                // Eliminar recursivamente todos los archivos y subcarpetas
                try (Stream<Path> paths = Files.walk(proveedoresFolderPath)) {
                    // Ordenar en reversa para eliminar primero los archivos y luego las carpetas
                    paths.sorted((a, b) -> b.compareTo(a))
                         .forEach(path -> {
                             try {
                                 if (!path.equals(proveedoresFolderPath)) {
                                     Files.delete(path);
                                     log.debug("Eliminado: {}", path);
                                 }
                             } catch (IOException e) {
                                 log.warn("No se pudo eliminar: {}", path, e);
                             }
                         });
                }

                log.info("Eliminación de archivos de proveedores completada");
            } else {
                log.info("La carpeta de proveedores no existe: {}", proveedoresFolderPath);
            }
        } catch (IOException e) {
            log.error("Error al eliminar archivos de proveedores", e);
            throw new RuntimeException("Error al eliminar archivos de proveedores: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un archivo Excel con el reporte detallado de la carga masiva.
     * 
     * @param response El objeto BulkUploadResponseDTO que contiene los resultados
     * @param fileName Nombre base para el archivo generado
     * @return El archivo generado como array de bytes
     */
    private byte[] generateReportFile(BulkUploadResponseDTO response, String fileName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Crear una hoja para el reporte
            Sheet sheet = workbook.createSheet("Reporte de carga");

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            Cell cellHeader1 = headerRow.createCell(0);
            cellHeader1.setCellValue("Fila en archivo original");
            Cell cellHeader2 = headerRow.createCell(1);
            cellHeader2.setCellValue("Estado");
            Cell cellHeader3 = headerRow.createCell(2);
            cellHeader3.setCellValue("Mensaje");

            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            cellHeader1.setCellStyle(headerStyle);
            cellHeader2.setCellStyle(headerStyle);
            cellHeader3.setCellStyle(headerStyle);

            // Estilos para diferentes estados
            CellStyle errorStyle = workbook.createCellStyle();
            Font errorFont = workbook.createFont();
            errorFont.setColor(IndexedColors.RED.getIndex());
            errorStyle.setFont(errorFont);

            CellStyle skippedStyle = workbook.createCellStyle();
            Font skippedFont = workbook.createFont();
            skippedFont.setColor(IndexedColors.ORANGE.getIndex());
            skippedStyle.setFont(skippedFont);

            CellStyle successStyle = workbook.createCellStyle();
            Font successFont = workbook.createFont();
            successFont.setColor(IndexedColors.GREEN.getIndex());
            successStyle.setFont(successFont);

            // Agregar los registros
            int rowNum = 1;

            // Primero los registros exitosos
            for (BulkUploadResponseDTO.SuccessRecord success : response.getSuccessful()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(success.getRowNumber());

                Cell statusCell = row.createCell(1);
                statusCell.setCellValue("ÉXITO");
                statusCell.setCellStyle(successStyle);

                Cell messageCell = row.createCell(2);
                messageCell.setCellValue(success.getDetails());
                messageCell.setCellStyle(successStyle);
            }

            // Luego los errores
            for (BulkUploadResponseDTO.ErrorRecord error : response.getErrors()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(error.getRowNumber());

                Cell statusCell = row.createCell(1);
                statusCell.setCellValue("ERROR");
                statusCell.setCellStyle(errorStyle);

                Cell messageCell = row.createCell(2);
                messageCell.setCellValue(error.getErrorMessage());
                messageCell.setCellStyle(errorStyle);
            }

            // Finalmente los registros omitidos
            for (BulkUploadResponseDTO.ErrorRecord skipped : response.getSkipped()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(skipped.getRowNumber());

                Cell statusCell = row.createCell(1);
                statusCell.setCellValue("OMITIDO");
                statusCell.setCellStyle(skippedStyle);

                Cell messageCell = row.createCell(2);
                messageCell.setCellValue(skipped.getErrorMessage());
                messageCell.setCellStyle(skippedStyle);
            }

            // Ajustar ancho de columnas
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            // Convertir el workbook a bytes
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            log.error("Error al generar archivo de reporte", e);
            throw new RuntimeException("Error al generar archivo de reporte: " + e.getMessage(), e);
        }
    }

    /**
     * Process a bulk upload of suppliers from a file.
     * Before processing, it cleans up all existing supplier data.
     * 
     * @param file The file containing supplier data (Excel)
     * @return Result of the processing operation with details about success and failures
     */
    public BulkUploadResponseDTO processBulkSupplierUpload(MultipartFile file) {
        log.info("Processing bulk supplier upload from file: {}", file.getOriginalFilename());

        // Limpiar datos existentes antes de la carga
        cleanAllProveedoresData();

        // Procesar el archivo Excel
        BulkUploadResponseDTO response = processExcelSupplierData(file);

        // Generar archivo de reporte en todos los casos
        byte[] reportFile = generateReportFile(response, "reporte_proveedores");
        response.setReportFile(reportFile);
        response.setReportFileName("reporte_proveedores_" + System.currentTimeMillis() + ".xlsx");

        return response;
    }

    /**
     * Procesa un archivo Excel con datos de proveedores.
     * Lee cada fila del archivo, valida los datos y crea objetos Proveedor.
     * 
     * @param file El archivo Excel con datos de proveedores
     * @return Resultado de la operación con detalles sobre éxitos y fallos
     */
    private BulkUploadResponseDTO processExcelSupplierData(MultipartFile file) {
        log.info("Processing Excel file with supplier data: {}", file.getOriginalFilename());

        BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .skippedCount(0)
                .errors(new ArrayList<>())
                .skipped(new ArrayList<>())
                .successful(new ArrayList<>())
                .build();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Obtener la primera hoja del libro
            Sheet sheet = workbook.getSheetAt(0);

            // Obtener el iterador de filas
            Iterator<Row> rowIterator = sheet.iterator();

            // Leer la primera fila (encabezados)
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Saltar la fila de encabezados
            }

            // Procesar cada fila
            int rowNumber = 1; // Empezamos en 1 porque ya saltamos la fila de encabezados
            int emptyRowCount = 0; // Contador de filas vacías consecutivas

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNumber++;

                // Verificar si la fila está vacía
                boolean isEmpty = isRowEmpty(row);

                if (rowNumber <= 3) {
                    log.info("Evaluando fila {} - vacía?: {}", rowNumber, isEmpty);
                }

                if (isEmpty) {
                    emptyRowCount++;
                    // Si hay más de 2 filas vacías consecutivas, detenemos el procesamiento
                    if (emptyRowCount > 2) {
                        log.info("Detectadas más de 2 filas vacías consecutivas. Finalizando procesamiento en fila {}", rowNumber);
                        break;
                    }
                    continue; // Saltamos esta fila vacía
                } else {
                    // Reiniciar contador de filas vacías si encontramos una fila con datos
                    emptyRowCount = 0;
                }

                response.setTotalRecords(response.getTotalRecords() + 1);

                try {
                    // Intentar procesar la fila
                    Proveedor proveedor = processExcelRow(row, response, rowNumber);

                    // Si el proveedor es null, significa que debe ser ignorado
                    // (ya se registró en el método processExcelRow)
                    if (proveedor == null) {
                        continue;
                    }

                    // Guardar el proveedor en la base de datos
                    proveedorRepo.save(proveedor);
                    response.setSuccessCount(response.getSuccessCount() + 1);

                    // Registrar el éxito
                    response.getSuccessful().add(
                        BulkUploadResponseDTO.SuccessRecord.builder()
                            .rowNumber(rowNumber)
                            .details("Proveedor guardado: " + proveedor.getNombre())
                            .build()
                    );

                } catch (Exception e) {
                    response.setFailureCount(response.getFailureCount() + 1);
                    response.getErrors().add(
                        BulkUploadResponseDTO.ErrorRecord.builder()
                            .rowNumber(rowNumber)
                            .errorMessage("Error al procesar fila: " + e.getMessage())
                            .build()
                    );
                    log.error("Error processing row {}: {}", rowNumber, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error processing Excel file: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar archivo Excel: " + e.getMessage(), e);
        }

        return response;
    }

    /**
     * Procesa una fila del archivo Excel y crea un objeto Proveedor.
     * 
     * @param row La fila del Excel a procesar
     * @param response El objeto de respuesta para registrar razones de omisión
     * @param rowNumber El número de fila actual
     * @return Objeto Proveedor creado a partir de los datos de la fila, o null si debe ser ignorado
     */
    private Proveedor processExcelRow(Row row, BulkUploadResponseDTO response, int rowNumber) {
        // Obtener los valores de las celdas
        String razonSocial = getCellValueAsString(row.getCell(0));
        String tipoIdentificacionStr = getCellValueAsString(row.getCell(1));
        String numeroIdentificacion = getCellValueAsString(row.getCell(2));
        String categoria = getCellValueAsString(row.getCell(3));
        String direccion = getCellValueAsString(row.getCell(4));
        String ciudad = getCellValueAsString(row.getCell(5));
        String departamento = getCellValueAsString(row.getCell(6));
        String numeroContacto = getCellValueAsString(row.getCell(7));
        String personaContacto = getCellValueAsString(row.getCell(8));
        String correo = getCellValueAsString(row.getCell(9));

        // Validar que exista tipo de identificación y número de identificación
        if (tipoIdentificacionStr == null || tipoIdentificacionStr.trim().isEmpty()) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Proveedor omitido: falta tipo de identificación")
                    .build()
            );
            return null; // Ignorar este proveedor
        }

        if (numeroIdentificacion == null || numeroIdentificacion.trim().isEmpty()) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Proveedor omitido: falta número de identificación")
                    .build()
            );
            return null; // Ignorar este proveedor
        }

        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Proveedor omitido: falta razón social")
                    .build()
            );
            return null; // Ignorar este proveedor
        }

        // Normalizar el número de identificación (eliminar puntos y espacios, mantener guiones)
        numeroIdentificacion = numeroIdentificacion.replace(".", "").replace(" ", "");

        // Crear el objeto Proveedor
        Proveedor proveedor = new Proveedor();

        // Establecer el tipo de identificación
        int tipoIdentificacion;
        if (tipoIdentificacionStr.equalsIgnoreCase("CC")) {
            tipoIdentificacion = 0; // Cédula de ciudadanía
        } else if (tipoIdentificacionStr.equalsIgnoreCase("NIT")) {
            tipoIdentificacion = 1; // NIT
        } else {
            tipoIdentificacion = 1; // Por defecto NIT
        }
        proveedor.setTipoIdentificacion(tipoIdentificacion);

        // Establecer el ID (número de identificación)
        proveedor.setId(numeroIdentificacion);

        // Establecer el nombre
        proveedor.setNombre(razonSocial);

        // Establecer dirección, ciudad y departamento
        proveedor.setDireccion(direccion);
        proveedor.setCiudad(ciudad);
        proveedor.setDepartamento(departamento);

        // Establecer categorías
        int[] categorias;
        if (categoria != null && !categoria.trim().isEmpty()) {
            if (categoria.toLowerCase().contains("materia prima")) {
                categorias = new int[]{1}; // Materias Primas
            } else if (categoria.toLowerCase().contains("empaque")) {
                categorias = new int[]{2}; // Materiales de Empaque
            } else if (categoria.toLowerCase().contains("servicios operativos")) {
                categorias = new int[]{0}; // Servicios Operativos
            } else if (categoria.toLowerCase().contains("servicios administrativos")) {
                categorias = new int[]{3}; // Servicios Administrativos
            } else if (categoria.toLowerCase().contains("equipos")) {
                categorias = new int[]{4}; // Equipos y Otros Servicios
            } else {
                categorias = new int[]{1}; // Por defecto Materias Primas
            }
        } else {
            categorias = new int[]{1}; // Por defecto Materias Primas
        }
        proveedor.setCategorias(categorias);

        // Establecer contactos
        List<Map<String, Object>> contactos = new ArrayList<>();
        if (numeroContacto != null && !numeroContacto.trim().isEmpty() ||
            personaContacto != null && !personaContacto.trim().isEmpty() ||
            correo != null && !correo.trim().isEmpty()) {

            Map<String, Object> contacto = new HashMap<>();
            if (numeroContacto != null && !numeroContacto.trim().isEmpty()) {
                contacto.put("telefono", numeroContacto);
            }
            if (personaContacto != null && !personaContacto.trim().isEmpty()) {
                contacto.put("nombre", personaContacto);
            }
            if (correo != null && !correo.trim().isEmpty()) {
                contacto.put("email", correo);
            }

            contactos.add(contacto);
        }
        proveedor.setContactos(contactos);

        // Establecer valores por defecto
        proveedor.setRegimenTributario(0); // Régimen común
        proveedor.setCondicionPago("0"); // Crédito

        return proveedor;
    }

    /**
     * Obtiene el valor de una celda como String.
     * 
     * @param cell La celda a procesar
     * @return El valor de la celda como String, o null si la celda es nula
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    // Convertir a String para evitar problemas con números formateados
                    // Eliminar el ".0" que se agrega a los números enteros
                    return String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", "");
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", "");
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            default:
                return null;
        }
    }

    /**
     * Obtiene el valor de una celda utilizando un índice seguro. Si el índice es
     * negativo o la celda es nula, retorna {@code null} para evitar excepciones.
     *
     * @param row   Fila del archivo Excel
     * @param index Índice de la columna a leer
     * @return Valor de la celda como cadena o {@code null} si no existe
     */
    private String getCellValue(Row row, int index) {
        if (index < 0) {
            return null;
        }
        Cell cell = row.getCell(index);
        return getCellValueAsString(cell);
    }

    /**
     * Verifica si una fila está vacía (todas sus celdas están vacías o son nulas)
     * 
     * @param row La fila a verificar
     * @return true si la fila está vacía, false en caso contrario
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        // Verificar si todas las celdas están vacías
        for (int i = 0; i < 10; i++) { // Verificamos las primeras 10 columnas (ajustar según necesidad)
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Limpia todos los datos relacionados con productos para permitir una carga masiva.
     * Elimina registros en el siguiente orden para mantener la integridad referencial:
     * 1. Transacciones de almacén y sus movimientos
     * 2. Órdenes de producción y seguimiento
     * 3. Insumos (recetas)
     * 4. Productos (Material, SemiTerminado, Terminado)
     */
    @Transactional
    public void cleanAllProductsData() {
        log.info("Iniciando limpieza de datos de productos para carga masiva");

        try {
            // Nivel 1: Eliminar transacciones de almacén y sus movimientos
            log.info("Eliminando transacciones de almacén y movimientos");
            transaccionAlmacenRepo.deleteAll(); // Elimina los movimientos
            transaccionAlmacenHeaderRepo.deleteAll(); // Elimina las cabeceras de transacción

            // Nivel 2: Eliminar órdenes de producción y seguimiento
            log.info("Eliminando órdenes de producción y seguimiento");
            ordenSeguimientoRepo.deleteAll(); // Primero eliminar el seguimiento
            ordenProduccionRepo.deleteAll(); // Luego eliminar las órdenes

            // Nivel 3: Eliminar insumos (recetas)
            log.info("Eliminando insumos (recetas)");
            insumoRepo.deleteAll();

            // Nivel 4: Finalmente eliminar los productos
            log.info("Eliminando productos");
            // No es necesario eliminar específicamente por tipo, ya que todos están en la misma tabla
            // con herencia de tabla única (SINGLE_TABLE)
            productoRepo.deleteAll();

            // Eliminar archivos de productos si es necesario
            deleteProductsFiles();

            log.info("Limpieza de datos de productos completada con éxito");
        } catch (Exception e) {
            log.error("Error durante la limpieza de datos de productos", e);
            throw new RuntimeException("Error al limpiar datos de productos: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina todos los archivos y carpetas en el directorio de productos.
     */
    private void deleteProductsFiles() {
        try {
            String baseDir = storageProperties.getUPLOAD_DIR();
            String productosDir = storageProperties.getPRODUCTOS();
            Path productosFolderPath = Paths.get(baseDir, productosDir);

            if (Files.exists(productosFolderPath)) {
                log.info("Eliminando archivos en la carpeta de productos: {}", productosFolderPath);

                // Eliminar recursivamente todos los archivos y subcarpetas
                try (Stream<Path> paths = Files.walk(productosFolderPath)) {
                    // Ordenar en reversa para eliminar primero los archivos y luego las carpetas
                    paths.sorted((a, b) -> b.compareTo(a))
                         .forEach(path -> {
                             try {
                                 if (!path.equals(productosFolderPath)) {
                                     Files.delete(path);
                                     log.debug("Eliminado: {}", path);
                                 }
                             } catch (IOException e) {
                                 log.warn("No se pudo eliminar: {}", path, e);
                             }
                         });
                }

                log.info("Eliminación de archivos de productos completada");
            } else {
                log.info("La carpeta de productos no existe: {}", productosFolderPath);
            }
        } catch (IOException e) {
            log.error("Error al eliminar archivos de productos", e);
            throw new RuntimeException("Error al eliminar archivos de productos: " + e.getMessage(), e);
        }
    }

    /**
     * Process a bulk upload of products from a file.
     * Before processing, it cleans up all existing product data.
     * 
     * @param file The file containing product data (Excel)
     * @return Result of the processing operation with details about success and failures
     */
    public BulkUploadResponseDTO processBulkProductUpload(MultipartFile file, MaterialBulkUploadMappingDTO mapping) {
        log.info("Processing bulk product upload from file: {}", file.getOriginalFilename());

        // Usar valores por defecto si no se proporciona mapeo
        if (mapping == null) {
            log.info("No se proporcionó mapeo, usando mapeo por defecto");
            mapping = new MaterialBulkUploadMappingDTO();
        } else {
            log.info("Usando mapeo enviado por el cliente");
        }

        log.info("Using mapping: {}", mapping);

        // Limpiar datos existentes antes de la carga
        cleanAllProductsData();

        // Procesar el archivo Excel
        BulkUploadResponseDTO response = processExcelProductData(file, mapping);

        // Generar archivo de reporte en todos los casos
        byte[] reportFile = generateReportFile(response, "reporte_productos");
        response.setReportFile(reportFile);
        response.setReportFileName("reporte_productos_" + System.currentTimeMillis() + ".xlsx");

        return response;
    }

    /**
     * Procesa un archivo Excel con datos de productos.
     * Lee cada fila del archivo, valida los datos y crea objetos Material.
     * Ignora materiales sin ID asignado en NUEVO CODIGO o con STOCK = 0.
     * Crea lotes con fecha de vencimiento a 6 meses.
     * Crea transacciones de almacén con movimientos para inicializar el inventario.
     * 
     * @param file El archivo Excel con datos de productos
     * @return Resultado de la operación con detalles sobre éxitos y fallos
     */

    /**
     * Procesa una fila del archivo Excel y crea un objeto Material.
     * 
     * @param row La fila del Excel a procesar
     * @param response El objeto de respuesta para registrar razones de omisión
     * @param rowNumber El número de fila actual
     * @return Objeto Material creado a partir de los datos de la fila, o null si debe ser ignorado
     */
    private Material processExcelMaterialRow(Row row, BulkUploadResponseDTO response, int rowNumber,
                                             MaterialBulkUploadMappingDTO mapping) {
        // Obtener los valores de las celdas
        String descripcion = getCellValue(row, mapping.getDescripcion());
        String unidadMedida = getCellValue(row, mapping.getUnidadMedida());
        String stock = getCellValue(row, mapping.getStock());
        String nuevoCodigo = getCellValue(row, mapping.getProductoId());
        String iva = getCellValue(row, mapping.getIva());
        String puntoReorden = getCellValue(row, mapping.getPuntoReorden());
        String costoUnitario = getCellValue(row, mapping.getCostoUnitario());

        // Logs de depuración para las primeras filas
        if (rowNumber <= 3) {
            log.info("Fila {} - descripcion: {}, unidadMedida: {}, stock: {}, productoId: {}, iva: {}, puntoReorden: {}, costoUnitario: {}",
                    rowNumber, descripcion, unidadMedida, stock, nuevoCodigo, iva, puntoReorden, costoUnitario);
        }

        // Validar que exista un nuevo código
        if (nuevoCodigo == null || nuevoCodigo.trim().isEmpty()) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Material omitido: falta código de producto")
                    .build()
            );
            return null; // Ignorar este material
        }

        // Validar que exista una descripción
        if (descripcion == null || descripcion.trim().isEmpty()) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Material omitido: falta descripción del producto")
                    .build()
            );
            return null; // Ignorar este material, la celda de nombre esta vacia
        }

        double stockValue = 0;
        try {
            // Manejar posibles formatos de número (con comas, etc.)
            if (stock != null && !stock.trim().isEmpty()) {
                String stockNormalized = stock.replace(",", ".");
                stockValue = Double.parseDouble(stockNormalized);
            }
        } catch (NumberFormatException e) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Material omitido: formato de stock inválido - " + stock)
                    .build()
            );
            return null; // Ignorar este material, Stock no tiene un formato valido
        }

        if (stockValue <= 0) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Material omitido: stock debe ser mayor que cero")
                    .build()
            );
            return null; // Ignorar este material, No se especifica Stock
        }

        // Validar el costo unitario
        double costoValue = 0.0;
        try {
            if (costoUnitario != null && !costoUnitario.trim().isEmpty()) {
                String costoNormalized = costoUnitario.replace(",", ".");
                costoValue = Double.parseDouble(costoNormalized);
            }
        } catch (NumberFormatException e) {
            // Si hay error de formato, se maneja como si fuera 0
            costoValue = 0;
        }

        // Si el costo es 0 o está vacío, ignorar el material
        if (costoValue <= 0) {
            response.setSkippedCount(response.getSkippedCount() + 1);
            response.getSkipped().add(
                BulkUploadResponseDTO.ErrorRecord.builder()
                    .rowNumber(rowNumber)
                    .errorMessage("Material omitido: costo unitario debe ser mayor que cero")
                    .build()
            );
            return null; // Ignorar este material
        }

        // Crear el objeto Material
        Material material = new Material();

        // Determinar el tipo de material basado en el prefijo del nuevo código
        if (nuevoCodigo.startsWith("me")) {
            material.setTipoMaterial(2); // Material de Empaque
        } else {
            material.setTipoMaterial(1); // Materia Prima
        }

        // Establecer el ID (nuevo código)
        // Usar directamente el código alfanumérico como ID del producto
        material.setProductoId(nuevoCodigo);

        // Establecer el nombre (descripción)
        material.setNombre(descripcion);

        // Establecer el tipo de unidades
        // Mapear unidades de medida a códigos de 4 caracteres o menos
        if (unidadMedida != null) {
            // Usar abreviaturas conocidas
            if (unidadMedida.equalsIgnoreCase("UNIDA") || unidadMedida.equalsIgnoreCase("UNIDAD") || unidadMedida.equalsIgnoreCase("UNIDADES")) {
                unidadMedida = "U";
            } else if (unidadMedida.equalsIgnoreCase("KILOGRAMO") || unidadMedida.equalsIgnoreCase("KILOGRAMOS")) {
                unidadMedida = "KG";
            } else if (unidadMedida.equalsIgnoreCase("LITRO") || unidadMedida.equalsIgnoreCase("LITROS")) {
                unidadMedida = "L";
            } else if (unidadMedida.equalsIgnoreCase("GRAMO") || unidadMedida.equalsIgnoreCase("GRAMOS")) {
                unidadMedida = "G";
            }

            // Si aún excede 4 caracteres, truncar
            if (unidadMedida.length() > 4) {
                unidadMedida = unidadMedida.substring(0, 4);
            }
        }
        material.setTipoUnidades(unidadMedida);

        // Establecer la cantidad por unidad (por defecto 1)
        material.setCantidadUnidad(1.0);

        // Establecer el IVA
        try {
            double ivaValue = Double.parseDouble(iva); // Guardar directamente como porcentaje
            material.setIvaPercentual(ivaValue);
        } catch (NumberFormatException | NullPointerException e) {
            material.setIvaPercentual(19); // Por defecto 19% como entero
        }

        // Establecer el costo
        material.setCosto(costoValue);

        // Establecer el punto de reorden
        double puntoReordenValue = -1; // Valor por defecto si está vacío
        try {
            if (puntoReorden != null && !puntoReorden.trim().isEmpty()) {
                String puntoReordenNormalized = puntoReorden.replace(",", ".");
                puntoReordenValue = Double.parseDouble(puntoReordenNormalized);
            }
        } catch (NumberFormatException e) {
            // Si hay error de formato, se mantiene el valor por defecto (-1)
        }
        material.setPuntoReorden(puntoReordenValue);

        return material;
    }

    private BulkUploadResponseDTO processExcelProductData(MultipartFile file, MaterialBulkUploadMappingDTO mapping) {
        log.info("Processing Excel file with product data: {}", file.getOriginalFilename());
        log.info("Using sheet '{}' for processing", mapping.getSheetName());
        log.info("Column mapping configuration: descripcion={}, unidadMedida={}, stock={}, productoId={}, iva={}, puntoReorden={}, costoUnitario={}",
                mapping.getDescripcion(), mapping.getUnidadMedida(), mapping.getStock(), 
                mapping.getProductoId(), mapping.getIva(), mapping.getPuntoReorden(), 
                mapping.getCostoUnitario());

        BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .skippedCount(0)
                .errors(new ArrayList<>())
                .skipped(new ArrayList<>())
                .successful(new ArrayList<>())
                .build();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Obtener la hoja indicada en el mapeo
            Sheet sheet = workbook.getSheet(mapping.getSheetName());
            if (sheet == null) { // manejo de errores en caso que la hoja no exista
                log.error("La hoja '{}' no existe en el archivo Excel", mapping.getSheetName());
                response.setFailureCount(1);
                response.getErrors().add(
                        BulkUploadResponseDTO.ErrorRecord.builder()
                                .rowNumber(0)
                                .errorMessage("Error: La hoja '" + mapping.getSheetName() + "' no existe en el archivo Excel")
                                .build()
                );
                return response;
            }

            // Obtener el iterador de filas
            Iterator<Row> rowIterator = sheet.iterator();

            // Leer la primera fila (encabezados)
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();

                // Log the header row (row 1)
                StringBuilder headerData = new StringBuilder();
                headerData.append("Fila 1 (Header) - Raw Excel data: ");
                for (int i = 0; i < 15; i++) { // Log first 15 columns
                    String cellValue = getCellValue(headerRow, i);
                    headerData.append(String.format("[Col %d: %s] ", i, cellValue));
                }
                log.info(headerData.toString());

                // Ya se ha leído la fila de encabezados
            }

            // Crear una transacción de almacén para la inicialización de inventario
            TransaccionAlmacen transaccionAlmacen = new TransaccionAlmacen();
            transaccionAlmacen.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OAA); // Orden de Ajuste de Almacén
            transaccionAlmacen.setIdEntidadCausante(0); // ID especial para inicialización
            transaccionAlmacen.setObservaciones("Inicialización de inventario desde carga masiva");
            transaccionAlmacen.setMovimientosTransaccion(new ArrayList<>());

            // Fecha actual para los lotes
            LocalDate currentDate = LocalDate.now();
            // Fecha de vencimiento (6 meses después)
            LocalDate expirationDate = currentDate.plusMonths(6);

            // Contador para generar batch numbers únicos
            int batchCounter = 1;

            // Procesar cada fila
            int rowNumber = 1; // Empezamos en 1 porque ya saltamos la fila de encabezados
            int emptyRowCount = 0; // Contador de filas vacías consecutivas

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNumber++;

                // Verificar si la fila está vacía
                boolean isEmpty = isRowEmpty(row);

                // Log raw data for the first 10 rows (rows 2-10, since row 1 is the header)
                if (rowNumber <= 10) {
                    log.info("Evaluando fila {} - vacía?: {}", rowNumber, isEmpty);

                    // Log raw Excel data for this row
                    StringBuilder rawData = new StringBuilder();
                    rawData.append(String.format("Fila %d - Raw Excel data: ", rowNumber));
                    for (int i = 0; i < 15; i++) { // Log first 15 columns
                        String cellValue = getCellValue(row, i);
                        rawData.append(String.format("[Col %d: %s] ", i, cellValue));
                    }
                    log.info(rawData.toString());
                }

                if (isEmpty) {
                    emptyRowCount++;
                    // Si hay más de 2 filas vacías consecutivas, detenemos el procesamiento
                    if (emptyRowCount > 2) {
                        log.info("Detectadas más de 2 filas vacías consecutivas. Finalizando procesamiento en fila {}", rowNumber);
                        break;
                    }
                    continue; // Saltamos esta fila vacía
                } else {
                    // Reiniciar contador de filas vacías si encontramos una fila con datos
                    emptyRowCount = 0;
                }

                response.setTotalRecords(response.getTotalRecords() + 1);

                try {
                    // Procesar la fila
                    Material material = processExcelMaterialRow(row, response, rowNumber, mapping);

                    // Si el material es null, significa que debe ser ignorado
                    // (ya se registró en el método processExcelMaterialRow)
                    if (material == null) {
                        continue;
                    }

                    // Guardar el material en la base de datos
                    materialRepo.save(material);

                    // Crear un lote para este material
                    String batchNumber = "INIT" + String.format("%06d", batchCounter++);
                    Lote lote = new Lote();
                    lote.setBatchNumber(batchNumber);
                    lote.setProductionDate(currentDate);
                    lote.setExpirationDate(expirationDate);
                    // No hay orden de compra ni producción asociada

                    // Guardar el lote en la base de datos
                    loteRepo.save(lote);

                    // Crear un movimiento para este material
                    Movimiento movimiento = new Movimiento();
                    movimiento.setProducto(material);
                    movimiento.setCantidad(Double.parseDouble(getCellValue(row, mapping.getStock()))); // STOCK
                    movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.COMPRA); // Usamos COMPRA como tipo para inicialización
                    movimiento.setAlmacen(Movimiento.Almacen.GENERAL);
                    movimiento.setLote(lote);
                    movimiento.setTransaccionAlmacen(transaccionAlmacen);

                    // Añadir el movimiento a la transacción
                    transaccionAlmacen.getMovimientosTransaccion().add(movimiento);

                    response.setSuccessCount(response.getSuccessCount() + 1);

                    // Registrar el éxito
                    response.getSuccessful().add(
                        BulkUploadResponseDTO.SuccessRecord.builder()
                            .rowNumber(rowNumber)
                            .details("Producto guardado: " + material.getNombre())
                            .build()
                    );

                } catch (Exception e) {
                    response.setFailureCount(response.getFailureCount() + 1);
                    response.getErrors().add(
                        BulkUploadResponseDTO.ErrorRecord.builder()
                            .rowNumber(rowNumber)
                            .errorMessage("Error al procesar fila: " + e.getMessage())
                            .build()
                    );
                    log.error("Error processing row {}: {}", rowNumber, e.getMessage());
                }
            }

            // Guardar la transacción de almacén si hay movimientos
            if (!transaccionAlmacen.getMovimientosTransaccion().isEmpty()) {
                transaccionAlmacenHeaderRepo.save(transaccionAlmacen);
            }

        } catch (Exception e) {
            log.error("Error processing Excel file: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar archivo Excel: " + e.getMessage(), e);
        }

        return response;
    }
}
