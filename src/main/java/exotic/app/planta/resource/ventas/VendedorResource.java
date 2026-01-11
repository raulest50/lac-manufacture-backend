package exotic.app.planta.resource.ventas;

import jakarta.persistence.EntityNotFoundException;
import exotic.app.planta.model.ventas.dto.CrearVendedorDTO;
import exotic.app.planta.model.ventas.dto.SearchVendedorDTO;
import exotic.app.planta.model.ventas.Vendedor;
import exotic.app.planta.service.ventas.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/vendedor")
@RequiredArgsConstructor
public class VendedorResource {

    private final VendedorService vendedorService;

    @PostMapping("/crear_vendedor")
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

    @PostMapping("/search_vendedores")
    public ResponseEntity<Page<Vendedor>> searchVendedores(
            @RequestBody SearchVendedorDTO searchDTO,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Vendedor> vendedores = vendedorService.searchVendedores(searchDTO, page, size);
        return ResponseEntity.ok(vendedores);
    }
}
