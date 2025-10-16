package lacosmetics.planta.lacmanufacture.service.ventas;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.ventas.dto.search.DTO_SearchCliente;
import lacosmetics.planta.lacmanufacture.model.ventas.Cliente;
import lacosmetics.planta.lacmanufacture.repo.ventas.ClienteRepo;
import lacosmetics.planta.lacmanufacture.service.commons.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClienteService {

    private final ClienteRepo clienteRepo;
    private final FileStorageService fileStorageService;

    /**
     * Guarda un cliente sin archivos adjuntos.
     * 
     * @param cliente El cliente a guardar
     * @return El cliente guardado
     */
    public Cliente saveCliente(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    /**
     * Guarda un cliente con sus archivos opcionales (RUT y Cámara de Comercio).
     * El método es transaccional para que si alguna parte falla, toda la transacción se revierta.
     *
     * @param cliente El cliente a guardar
     * @param rutFile Archivo RUT opcional
     * @param camaraFile Archivo Cámara de Comercio opcional
     * @return El cliente guardado
     * @throws IOException Si ocurre un error al guardar los archivos
     */
    @Transactional
    public Cliente saveClienteWithFiles(Cliente cliente,
                                        MultipartFile rutFile,
                                        MultipartFile camaraFile) throws IOException {

        // Primero guardamos el cliente para obtener su ID
        Cliente savedCliente = clienteRepo.save(cliente);

        // Guardamos los archivos si se proporcionan
        if (rutFile != null && !rutFile.isEmpty()) {
            String rutPath = fileStorageService.storeFileCliente(savedCliente.getClienteId(), rutFile, "rut.pdf");
            savedCliente.setUrlRut(rutPath);
        }

        if (camaraFile != null && !camaraFile.isEmpty()) {
            String camaraPath = fileStorageService.storeFileCliente(savedCliente.getClienteId(), camaraFile, "camara.pdf");
            savedCliente.setUrlCamComer(camaraPath);
        }

        // Guardamos el cliente con las URLs actualizadas
        return clienteRepo.save(savedCliente);
    }

    public Optional<Cliente> findClienteById(int id) {
        return clienteRepo.findById(id);
    }

    /**
     * Busca clientes por nombre o email (búsqueda simple).
     * 
     * @param q Texto a buscar
     * @return Lista de clientes que coinciden con la búsqueda
     */
    public List<Cliente> searchClientes(String q) {
        return clienteRepo.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }

    /**
     * Busca clientes con paginación según los criterios especificados en el DTO.
     * 
     * @param searchDTO DTO con los criterios de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @return Página de clientes que coinciden con la búsqueda
     */
    public Page<Cliente> searchClientes(DTO_SearchCliente searchDTO, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Búsqueda por ID
        if (searchDTO.getSearchType() == DTO_SearchCliente.SearchType.ID && searchDTO.getId() != null) {
            return clienteRepo.findById(searchDTO.getId(), pageable);
        }

        // Búsqueda por nombre o email
        if (searchDTO.getSearchType() == DTO_SearchCliente.SearchType.NOMBRE_O_EMAIL) {
            String nombre = (searchDTO.getNombre() != null && !isBlank(searchDTO.getNombre())) 
                    ? searchDTO.getNombre() 
                    : null;

            String email = (searchDTO.getEmail() != null && !isBlank(searchDTO.getEmail())) 
                    ? searchDTO.getEmail() 
                    : null;

            return clienteRepo.searchByNombreOrEmail(nombre, email, pageable);
        }

        // Caso por defecto: retornar página vacía
        return Page.empty(pageable);
    }

    /**
     * Actualiza un cliente existente.
     * 
     * @param id ID del cliente a actualizar
     * @param data Datos actualizados del cliente
     * @return Cliente actualizado
     * @throws RuntimeException Si el cliente no existe
     */
    public Cliente updateCliente(int id, Cliente data) {
        Cliente existing = clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente not found"));
        data.setClienteId(id);
        return clienteRepo.save(data);
    }

    /**
     * Actualiza un cliente existente con sus archivos opcionales.
     * 
     * @param id ID del cliente a actualizar
     * @param cliente Datos actualizados del cliente
     * @param rutFile Archivo RUT opcional
     * @param camaraFile Archivo Cámara de Comercio opcional
     * @return Cliente actualizado
     * @throws IOException Si ocurre un error al guardar los archivos
     * @throws IllegalArgumentException Si el cliente no existe o hay errores de validación
     */
    @Transactional
    public Cliente updateClienteWithFiles(int id,
                                         Cliente cliente,
                                         MultipartFile rutFile,
                                         MultipartFile camaraFile) throws IOException {

        // Verificar que el cliente existe
        if (!clienteRepo.existsById(id)) {
            throw new IllegalArgumentException("No existe un Cliente con el Id: " + id);
        }

        // Validar que el ID en la URL coincida con el ID en el cuerpo de la solicitud
        if (id != cliente.getClienteId()) {
            throw new IllegalArgumentException("El ID en la URL no coincide con el ID en el cuerpo de la solicitud");
        }

        // Obtener el cliente original para preservar campos que no deben modificarse
        Cliente clienteOriginal = clienteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));

        // Validar archivos si se proporcionan
        if (rutFile != null && !rutFile.isEmpty() && !rutFile.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("El archivo RUT debe ser un PDF");
        }

        if (camaraFile != null && !camaraFile.isEmpty() && !camaraFile.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("El archivo Cámara de Comercio debe ser un PDF");
        }

        // Actualizar campos editables
        clienteOriginal.setNombre(cliente.getNombre());
        clienteOriginal.setEmail(cliente.getEmail());
        clienteOriginal.setTelefono(cliente.getTelefono());
        clienteOriginal.setDireccion(cliente.getDireccion());
        clienteOriginal.setCondicionesPago(cliente.getCondicionesPago());
        clienteOriginal.setLimiteCredito(cliente.getLimiteCredito());

        // Registrar en log los cambios realizados
        log.info("Actualizando cliente {}: nombre={}, email={}", 
                 id, cliente.getNombre(), cliente.getEmail());

        // Guardar archivos si se proporcionan
        if (rutFile != null && !rutFile.isEmpty()) {
            String rutPath = fileStorageService.storeFileCliente(id, rutFile, "rut.pdf");
            clienteOriginal.setUrlRut(rutPath);
            log.info("Actualizado archivo RUT para cliente: {}", id);
        }

        if (camaraFile != null && !camaraFile.isEmpty()) {
            String camaraPath = fileStorageService.storeFileCliente(id, camaraFile, "camara.pdf");
            clienteOriginal.setUrlCamComer(camaraPath);
            log.info("Actualizado archivo Cámara de Comercio para cliente: {}", id);
        }

        // Guardar el cliente actualizado
        return clienteRepo.save(clienteOriginal);
    }

    public void deleteCliente(int id) {
        clienteRepo.deleteById(id);
    }

    /**
     * Verifica si una cadena es nula, vacía o contiene solo espacios en blanco.
     *
     * @param str La cadena a verificar
     * @return true si la cadena es nula, vacía o contiene solo espacios en blanco
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
