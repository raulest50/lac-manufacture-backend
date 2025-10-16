package lacosmetics.planta.lacmanufacture.model.commons.dto.bulkupload;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing the response from a bulk upload operation.
 * Contains information about the number of records processed, successful uploads,
 * failures, and error messages for failed records.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResponseDTO {

    /**
     * Total number of records found in the uploaded file
     */
    private int totalRecords;

    /**
     * Number of records successfully processed and saved
     */
    private int successCount;

    /**
     * Number of records that failed to process
     */
    private int failureCount;

    /**
     * Number of records that were skipped (not processed but not counted as errors)
     */
    private int skippedCount;

    /**
     * List of error messages for failed records
     */
    @Builder.Default
    private List<ErrorRecord> errors = new ArrayList<>();

    /**
     * List of skipped records with reasons
     */
    @Builder.Default
    private List<ErrorRecord> skipped = new ArrayList<>();

    /**
     * List of successfully processed records
     */
    @Builder.Default
    private List<SuccessRecord> successful = new ArrayList<>();

    /**
     * Archivo de reporte (Excel) con detalles de todos los registros procesados
     */
    private byte[] reportFile;

    /**
     * Nombre del archivo de reporte
     */
    private String reportFileName;

    /**
     * Inner class representing an error record with row number and error message
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorRecord {
        /**
         * Row number in the original file where the error occurred
         */
        private int rowNumber;

        /**
         * Error message describing the issue
         */
        private String errorMessage;
    }

    /**
     * Inner class representing a successful record with row number and details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessRecord {
        /**
         * Row number in the original file
         */
        private int rowNumber;

        /**
         * Additional details about the successful record
         */
        private String details;
    }
}
