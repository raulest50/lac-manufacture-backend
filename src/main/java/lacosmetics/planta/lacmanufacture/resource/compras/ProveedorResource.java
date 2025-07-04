package lacosmetics.planta.lacmanufacture.resource.compras;


import com.fasterxml.jackson.databind.ObjectMapper;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.model.dto.search.DTO_SearchProveedor;
import lacosmetics.planta.lacmanufacture.service.compras.ProveedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@Slf4j
public class ProveedorResource {

    private final ProveedorService proveedorService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Proveedor> saveProveedor(
            @RequestPart("proveedor") String proveedorJson,
            @RequestPart(value = "rutFile", required = false) MultipartFile rutFile,
            @RequestPart(value = "camaraFile", required = false) MultipartFile camaraFile
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Proveedor proveedor = mapper.readValue(proveedorJson, Proveedor.class);
            // Delegate all logic to the service method.
            Proveedor saved = proveedorService.saveProveedorWithFiles(proveedor, rutFile, camaraFile);
            return ResponseEntity.created(URI.create("/proveedor/" + saved.getId())).body(saved);
        } catch (IOException e) {
            // Log error as needed and return error response.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }



    @GetMapping("/search")
    public ResponseEntity<List<Proveedor>> searchProveedores(@RequestParam("q") String searchText) {
        List<Proveedor> proveedores = proveedorService.searchProveedores(searchText);
        return ResponseEntity.ok(proveedores);
    }

    @PostMapping("/search_pag")
    public ResponseEntity<Page<Proveedor>> searchProveedores(
            @RequestBody DTO_SearchProveedor searchDTO,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            Page<Proveedor> proveedores = proveedorService.searchProveedores(searchDTO, page, size);
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            // Return an empty page with the same pageable
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(Page.empty(pageable));
        }
    }

    /**
     * Endpoint para actualizar un proveedor existente.
     * Permite modificar campos como régimen tributario, condición de pago, contactos,
     * y actualizar documentos como RUT y Cámara de Comercio.
     * 
     * @param id ID del proveedor a actualizar
     * @param proveedorJson JSON con los datos actualizados del proveedor
     * @param rutFile Archivo RUT actualizado (opcional)
     * @param camaraFile Archivo Cámara de Comercio actualizado (opcional)
     * @return El proveedor actualizado
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateProveedor(
            @PathVariable String id,
            @RequestPart("proveedor") String proveedorJson,
            @RequestPart(value = "rutFile", required = false) MultipartFile rutFile,
            @RequestPart(value = "camaraFile", required = false) MultipartFile camaraFile
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Proveedor proveedor = mapper.readValue(proveedorJson, Proveedor.class);

            // Delegar toda la lógica al método del servicio
            Proveedor updated = proveedorService.updateProveedorWithFiles(id, proveedor, rutFile, camaraFile);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al actualizar el proveedor: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Error al procesar los archivos del proveedor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar los archivos: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar el proveedor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el proveedor: " + e.getMessage()));
        }
    }
}
