package lacosmetics.planta.lacmanufacture.service.ventas;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.ventas.dto.CrearVendedorDTO;
import lacosmetics.planta.lacmanufacture.model.ventas.dto.SearchVendedorDTO;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.model.ventas.Vendedor;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.repo.ventas.VendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final UserRepository userRepository;

    public Vendedor create(CrearVendedorDTO request) {
        if (vendedorRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("Ya existe un vendedor con la cédula " + request.getCedula());
        }

        if (request.getUserId() == null) {
            throw new IllegalArgumentException("El identificador del usuario asociado es obligatorio");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario con el id " + request.getUserId()));

        Vendedor vendedor = Vendedor.builder()
                .cedula(request.getCedula())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .fechaNacimiento(request.getFechaNacimiento())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .user(user)
                .build();

        return vendedorRepository.save(vendedor);
    }

    public Page<Vendedor> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vendedorRepository.findAll(pageable);
    }

    public Page<Vendedor> searchVendedores(SearchVendedorDTO searchDTO, int page, int size) {
        // Validar parámetros de paginación
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        // Crear objeto Pageable para paginación
        Pageable pageable = PageRequest.of(page, size);

        // Si no hay criterios de búsqueda o el texto de búsqueda está vacío, devolver todos los vendedores
        if (searchDTO == null || searchDTO.getSearchType() == null || 
            searchDTO.getSearch() == null || searchDTO.getSearch().trim().isEmpty()) {
            return vendedorRepository.findAll(pageable);
        }

        switch (searchDTO.getSearchType()) {
            case ID:
                try {
                    // Buscar por cédula (convertir a long)
                    long cedula = Long.parseLong(searchDTO.getSearch());
                    return vendedorRepository.findByCedula(cedula, pageable);
                } catch (NumberFormatException e) {
                    // Si no es un número válido, devolver página vacía
                    return Page.empty(pageable);
                }

            case NAME:
                // Buscar por coincidencia parcial del nombre o apellido
                String searchTerm = searchDTO.getSearch().toLowerCase();
                return vendedorRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
                    searchTerm, searchTerm, pageable);

            default:
                return Page.empty(pageable);
        }
    }
}
