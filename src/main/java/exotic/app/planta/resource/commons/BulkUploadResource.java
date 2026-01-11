package exotic.app.planta.resource.commons;

import exotic.app.planta.model.commons.dto.bulkupload.BulkUploadResponseDTO;
import exotic.app.planta.model.commons.dto.bulkupload.MaterialBulkUploadMappingDTO;
import exotic.app.planta.service.commons.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
     * @return Response with status of the upload operation or a file with detailed report
     */
    @PostMapping("/proveedores")
    public ResponseEntity<?> bulkUploadSuppliers(@RequestParam("file") MultipartFile file) {
        BulkUploadResponseDTO response = bulkUploadService.processBulkSupplierUpload(file);

        // Siempre devolver el archivo de reporte
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", response.getReportFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getReportFile());
    }

    /**
     * Endpoint for bulk upload of products.
     * Accepts a CSV or Excel file containing product data.
     * 
     * @param file The file containing product data
     * @return Response with status of the upload operation or a file with detailed report
     */
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUploadProducts(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "mapping", required = false) MaterialBulkUploadMappingDTO mapping) {
        BulkUploadResponseDTO response = bulkUploadService.processBulkProductUpload(file, mapping);

        // Siempre devolver el archivo de reporte
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", response.getReportFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getReportFile());
    }
}
