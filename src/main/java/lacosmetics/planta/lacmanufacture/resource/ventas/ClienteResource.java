package lacosmetics.planta.lacmanufacture.resource.ventas;

import lacosmetics.planta.lacmanufacture.model.ventas.Cliente;
import lacosmetics.planta.lacmanufacture.service.ventas.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteResource {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> saveCliente(@RequestBody Cliente cliente) {
        Cliente saved = clienteService.saveCliente(cliente);
        return ResponseEntity.created(URI.create("/clientes/" + saved.getClienteId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable int id) {
        return clienteService.findClienteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> searchClientes(@RequestParam("q") String q) {
        return ResponseEntity.ok(clienteService.searchClientes(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable int id, @RequestBody Cliente cliente) {
        Cliente updated = clienteService.updateCliente(id, cliente);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable int id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
