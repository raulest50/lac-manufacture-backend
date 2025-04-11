package lacosmetics.planta.lacmanufacture.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
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

    // In ProveedorResource.java
    @GetMapping("/search_pag")
    public ResponseEntity<Page<Proveedor>> searchProveedores(
            @RequestParam("q") String searchText,
            @RequestParam("searchType") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Proveedor> proveedores = proveedorService.searchProveedores(searchText, searchType, page, size);
        return ResponseEntity.ok(proveedores);
    }


}
