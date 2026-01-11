package exotic.app.planta.resource.ventas;

import com.fasterxml.jackson.databind.ObjectMapper;
import exotic.app.planta.model.ventas.dto.search.DTO_SearchCliente;
import exotic.app.planta.model.ventas.Cliente;
import exotic.app.planta.service.ventas.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteResource {

    private final ClienteService clienteService;
    private final ObjectMapper objectMapper;

    /**
     * Endpoint para crear un cliente con archivos opcionales (RUT y Cámara de Comercio).
     * 
     * @param clienteJson JSON con los datos del cliente
     * @param rutFile Archivo RUT opcional
     * @param camaraFile Archivo Cámara de Comercio opcional
     * @return El cliente creado
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> saveCliente(
            @RequestPart("cliente") String clienteJson,
            @RequestPart(value = "rutFile", required = false) MultipartFile rutFile,
            @RequestPart(value = "camaraFile", required = false) MultipartFile camaraFile
    ) {
        try {
            // Convertir el JSON a objeto Cliente
            Cliente cliente = objectMapper.readValue(clienteJson, Cliente.class);

            // Guardar el cliente con sus archivos
            Cliente saved = clienteService.saveClienteWithFiles(cliente, rutFile, camaraFile);

            return ResponseEntity.created(URI.create("/clientes/" + saved.getClienteId())).body(saved);
        } catch (IOException e) {
            log.error("Error al procesar los archivos del cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar los archivos: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al guardar el cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar el cliente: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para crear un cliente sin archivos (método simple).
     * 
     * @param cliente Datos del cliente
     * @return El cliente creado
     */
    @PostMapping
    public ResponseEntity<Cliente> saveClienteSimple(@RequestBody Cliente cliente) {
        Cliente saved = clienteService.saveCliente(cliente);
        return ResponseEntity.created(URI.create("/clientes/" + saved.getClienteId())).body(saved);
    }

    /**
     * Obtiene un cliente por su ID.
     * 
     * @param id ID del cliente
     * @return El cliente si existe, o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable int id) {
        return clienteService.findClienteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Búsqueda simple de clientes por nombre o email.
     * 
     * @param q Texto a buscar
     * @return Lista de clientes que coinciden con la búsqueda
     */
    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> searchClientes(@RequestParam("q") String q) {
        return ResponseEntity.ok(clienteService.searchClientes(q));
    }

    /**
     * Búsqueda avanzada de clientes con paginación.
     * 
     * @param searchDTO DTO con los criterios de búsqueda
     * @param page Número de página (por defecto 0)
     * @param size Tamaño de página (por defecto 10)
     * @return Página de clientes que coinciden con la búsqueda
     */
    @PostMapping("/search_pag")
    public ResponseEntity<Page<Cliente>> searchClientesPaginado(
            @RequestBody DTO_SearchCliente searchDTO,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            Page<Cliente> clientes = clienteService.searchClientes(searchDTO, page, size);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            log.error("Error al buscar clientes: {}", e.getMessage(), e);
            return ResponseEntity.ok(Page.empty());
        }
    }

    /**
     * Actualiza un cliente existente (método simple).
     * 
     * @param id ID del cliente a actualizar
     * @param cliente Datos actualizados del cliente
     * @return El cliente actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateClienteSimple(@PathVariable int id, @RequestBody Cliente cliente) {
        try {
            Cliente updated = clienteService.updateCliente(id, cliente);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error al actualizar el cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza un cliente existente con archivos opcionales.
     * 
     * @param id ID del cliente a actualizar
     * @param clienteJson JSON con los datos actualizados del cliente
     * @param rutFile Archivo RUT opcional
     * @param camaraFile Archivo Cámara de Comercio opcional
     * @return El cliente actualizado
     */
    @PutMapping(value = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateClienteWithFiles(
            @PathVariable int id,
            @RequestPart("cliente") String clienteJson,
            @RequestPart(value = "rutFile", required = false) MultipartFile rutFile,
            @RequestPart(value = "camaraFile", required = false) MultipartFile camaraFile
    ) {
        try {
            // Convertir el JSON a objeto Cliente
            Cliente cliente = objectMapper.readValue(clienteJson, Cliente.class);

            // Actualizar el cliente con sus archivos
            Cliente updated = clienteService.updateClienteWithFiles(id, cliente, rutFile, camaraFile);

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al actualizar el cliente: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Error al procesar los archivos del cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar los archivos: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar el cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el cliente: " + e.getMessage()));
        }
    }

    /**
     * Elimina un cliente existente.
     * 
     * @param id ID del cliente a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable int id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
