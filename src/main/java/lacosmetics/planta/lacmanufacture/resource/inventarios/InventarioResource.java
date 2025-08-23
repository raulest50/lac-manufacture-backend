package lacosmetics.planta.lacmanufacture.resource.inventarios;

import lacosmetics.planta.lacmanufacture.model.dto.InventarioExcelRequestDTO;
import lacosmetics.planta.lacmanufacture.service.inventarios.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioResource {

    private final InventarioService inventarioService;

    @PostMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody InventarioExcelRequestDTO dto) {
        byte[] excel = inventarioService.generateInventoryExcel(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inventario.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
