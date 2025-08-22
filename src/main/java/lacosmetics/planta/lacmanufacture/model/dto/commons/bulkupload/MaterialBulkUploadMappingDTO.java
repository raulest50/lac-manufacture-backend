package lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload;

import lombok.Data;

/**
 * DTO para definir el mapeo de columnas en la carga masiva de materiales.
 * Cada propiedad representa el índice de la columna en el archivo Excel
 * que contiene la información correspondiente. Los valores por defecto
 * mantienen compatibilidad con el formato histórico.
 */
@Data
public class MaterialBulkUploadMappingDTO {

    /** Nombre de la hoja de cálculo que contiene los datos. */
    private String sheetName = "inventario";

    /** Índice de la columna de descripción del producto. */
    private int descripcion = 2;

    /** Índice de la columna de unidad de medida. */
    private int unidadMedida = 4;

    /** Índice de la columna de stock inicial. */
    private int stock = 7;

    /** Índice de la columna del identificador del producto. */
    private int productoId = 0;

    /** Índice de la columna del IVA. */
    private int iva = 10;

    /** Índice de la columna del punto de reorden. */
    private int puntoReorden = 8;

    /** Índice de la columna del costo unitario. */
    private int costoUnitario = 11;
}
