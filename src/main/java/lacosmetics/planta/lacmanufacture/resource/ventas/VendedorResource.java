package lacosmetics.planta.lacmanufacture.resource.ventas;

import jakarta.persistence.EntityNotFoundException;
import lacosmetics.planta.lacmanufacture.model.dto.ventas.CrearVendedorDTO;
import lacosmetics.planta.lacmanufacture.model.ventas.Vendedor;
import lacosmetics.planta.lacmanufacture.service.ventas.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VendedorResource {

    private final VendedorService vendedorService;

    @PostMapping("/vendedores")
    public ResponseEntity<?> createVendedor(@RequestBody CrearVendedorDTO request) {
        try {
            Vendedor saved = vendedorService.create(request);
            return ResponseEntity.created(URI.create("/ventas/vendedores/" + saved.getCedula())).body(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/vendedores")
    public ResponseEntity<Page<Vendedor>> listVendedores(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Vendedor> vendedores = vendedorService.listAll(page, size);
        return ResponseEntity.ok(vendedores);
    }
}
