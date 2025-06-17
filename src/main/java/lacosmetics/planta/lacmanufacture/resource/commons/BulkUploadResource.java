package lacosmetics.planta.lacmanufacture.resource.commons;

import lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload.BulkUploadResponseDTO;
import lacosmetics.planta.lacmanufacture.service.commons.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling bulk uploads of suppliers and products.
 * This resource provides endpoints for uploading CSV or Excel files containing
 * multiple suppliers or products to be processed in batch.
 */
@RestController
@RequestMapping("/api/bulk-upload")
@RequiredArgsConstructor
public class BulkUploadResource {

    private final BulkUploadService bulkUploadService;

    /**
     * Endpoint for bulk upload of suppliers.
     * Accepts a CSV or Excel file containing supplier data.
     * 
     * @param file The file containing supplier data
     * @return Response with status of the upload operation
     */
    @PostMapping("/proveedores")
    public ResponseEntity<BulkUploadResponseDTO> bulkUploadSuppliers(@RequestParam("file") MultipartFile file) {
        // The implementation will be added later
        BulkUploadResponseDTO response = bulkUploadService.processBulkSupplierUpload(file);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for bulk upload of products.
     * Accepts a CSV or Excel file containing product data.
     * 
     * @param file The file containing product data
     * @return Response with status of the upload operation
     */
    @PostMapping("/products")
    public ResponseEntity<BulkUploadResponseDTO> bulkUploadProducts(@RequestParam("file") MultipartFile file) {
        // The implementation will be added later
        BulkUploadResponseDTO response = bulkUploadService.processBulkProductUpload(file);
        return ResponseEntity.ok(response);
    }
}
