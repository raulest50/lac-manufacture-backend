package lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
     * List of error messages for failed records
     */
    @Builder.Default
    private List<ErrorRecord> errors = new ArrayList<>();
    
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
}