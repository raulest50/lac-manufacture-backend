package lacosmetics.planta.lacmanufacture.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    ) throws IOException {
        // Convert JSON string to Proveedor object
        ObjectMapper mapper = new ObjectMapper();
        Proveedor proveedor = mapper.readValue(proveedorJson, Proveedor.class);

        // If any file is provided, create a folder at /data/proveedores/{nit}/
        if ((rutFile != null && !rutFile.isEmpty()) || (camaraFile != null && !camaraFile.isEmpty())) {
            File folder = new File("/data/proveedores/" + proveedor.getId());
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Save RUT file if present and update rutUrl
            if (rutFile != null && !rutFile.isEmpty()) {
                File rutDestination = new File(folder, "rut.pdf");
                rutFile.transferTo(rutDestination);
                proveedor.setRutUrl("/data/proveedores/" + proveedor.getId() + "/rut.pdf");
            }

            // Save Cámara y Comercio file if present and update camaraUrl
            if (camaraFile != null && !camaraFile.isEmpty()) {
                File camaraDestination = new File(folder, "camara.pdf");
                camaraFile.transferTo(camaraDestination);
                proveedor.setCamaraUrl("/data/proveedores/" + proveedor.getId() + "/camara.pdf");
            }
        }

        Proveedor saved = proveedorService.saveProveedor(proveedor);
        return ResponseEntity.created(URI.create("/proveedor/" + saved.getId())).body(saved);
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
