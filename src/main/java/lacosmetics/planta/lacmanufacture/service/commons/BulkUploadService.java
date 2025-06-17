package lacosmetics.planta.lacmanufacture.service.commons;

import lacosmetics.planta.lacmanufacture.config.StorageProperties;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload.BulkUploadResponseDTO;
import lacosmetics.planta.lacmanufacture.repo.compras.FacturaCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return processExcelSupplierData(file);
    }

    /**
     * Procesa un archivo Excel con datos de proveedores.
     * Lee cada fila del archivo, valida los datos y crea objetos Proveedor.
     * Ignora proveedores sin tipo de identificación o número de identificación.
     * Normaliza los números de identificación eliminando puntos y espacios, pero manteniendo guiones.
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
                .errors(new ArrayList<>())
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
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNumber++;
                response.setTotalRecords(response.getTotalRecords() + 1);

                try {
                    // Intentar procesar la fila
                    Proveedor proveedor = processExcelRow(row);

                    // Si el proveedor es null, significa que debe ser ignorado
                    if (proveedor == null) {
                        response.setFailureCount(response.getFailureCount() + 1);
                        response.getErrors().add(
                            BulkUploadResponseDTO.ErrorRecord.builder()
                                .rowNumber(rowNumber)
                                .errorMessage("Proveedor ignorado: falta tipo de identificación o número de identificación")
                                .build()
                        );
                        continue;
                    }

                    // Guardar el proveedor en la base de datos
                    proveedorRepo.save(proveedor);
                    response.setSuccessCount(response.getSuccessCount() + 1);

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
     * @return Objeto Proveedor creado a partir de los datos de la fila, o null si debe ser ignorado
     */
    private Proveedor processExcelRow(Row row) {
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
        if (tipoIdentificacionStr == null || tipoIdentificacionStr.trim().isEmpty() ||
            numeroIdentificacion == null || numeroIdentificacion.trim().isEmpty()) {
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
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            default:
                return null;
        }
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
    public BulkUploadResponseDTO processBulkProductUpload(MultipartFile file) {
        log.info("Processing bulk product upload from file: {}", file.getOriginalFilename());

        // Limpiar datos existentes antes de la carga
        cleanAllProductsData();

        // Procesar el archivo Excel
        return processExcelProductData(file);
    }

    /**
     * Procesa un archivo Excel con datos de productos.
     * Lee cada fila del archivo, valida los datos y crea objetos Producto.
     * 
     * Este método será implementado posteriormente.
     * 
     * @param file El archivo Excel con datos de productos
     * @return Resultado de la operación con detalles sobre éxitos y fallos
     */
    private BulkUploadResponseDTO processExcelProductData(MultipartFile file) {
        log.info("Processing Excel file with product data: {}", file.getOriginalFilename());

        // Este método será implementado posteriormente
        // Debe leer el archivo Excel, procesar cada fila y crear los objetos Producto correspondientes
        // Debe manejar los diferentes tipos de productos (Material, SemiTerminado, Terminado)
        // Debe validar los datos y manejar errores

        return BulkUploadResponseDTO.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .errors(new ArrayList<>())
                .build();
    }
}
