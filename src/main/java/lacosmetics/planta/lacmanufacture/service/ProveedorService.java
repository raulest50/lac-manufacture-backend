package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.model.dto.search.DTO_SearchProveedor;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProveedorService {

    @Autowired
    private final ProveedorRepo proveedorRepo;

    private final FileStorageService fileStorageService;


    /**
     * Saves a Proveedor and its optional files (RUT and Cámara).
     * The method is transactional so that if any part fails, the whole transaction is rolled back.
     *
     * @param proveedor The Proveedor entity populated from the request.
     * @param rutFile   The optional RUT file.
     * @param camaraFile The optional Cámara file.
     * @return The saved Proveedor entity.
     * @throws IOException if file storage fails.
     */
    @Transactional
    public Proveedor saveProveedorWithFiles(Proveedor proveedor,
                                            MultipartFile rutFile,
                                            MultipartFile camaraFile) throws IOException {

        // se revisa que no existe previamente el proveedor que se desea registrar.
        if(proveedorRepo.existsById(proveedor.getId())) throw new IllegalArgumentException("Ya existe un Proveedor con el Id" + proveedor.getId());

        // Save files if provided using the file storage service.
        if (rutFile != null && !rutFile.isEmpty()) {
            // Save file to /data/proveedores/{proveedorId}/rut.pdf and update the URL.
            String rutPath = fileStorageService.storeFileProveedor(proveedor.getId(), rutFile, "rut.pdf");
            proveedor.setRutUrl(rutPath);
        }
        if (camaraFile != null && !camaraFile.isEmpty()) {
            String camaraPath = fileStorageService.storeFileProveedor(proveedor.getId(), camaraFile, "camara.pdf");
            proveedor.setCamaraUrl(camaraPath);
        }
        // Save the proveedor entity; if any exception occurs (including in file saving),
        // the whole transaction will roll back.
        return proveedorRepo.save(proveedor);
    }




    public List<Proveedor> searchProveedores(String searchText) {
        List<Proveedor> result = new ArrayList<>();

        // Search by nombre containing the search text
        result.addAll(proveedorRepo.findByNombreContainingIgnoreCase(searchText));

        // Remove duplicates if an entity matched both name and id
        return result.stream().distinct().collect(Collectors.toList());
    }

    public Page<Proveedor> searchProveedores(String searchText, String searchType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if ("nombre".equalsIgnoreCase(searchType)) {
            return proveedorRepo.findByNombreContainingIgnoreCase(searchText, pageable);
        } else if ("nit".equalsIgnoreCase(searchType)) {
            try {
                int nit = Integer.parseInt(searchText);
                return proveedorRepo.findById(nit, pageable);
            } catch (NumberFormatException e) {
                return Page.empty(pageable);
            }
        } else {
            return Page.empty(pageable);
        }
    }

    /**
     * Search for providers based on the search criteria in the DTO
     *
     * @param searchDTO The search criteria
     * @param page The page number
     * @param size The page size
     * @return A page of providers matching the search criteria
     */
    public Page<Proveedor> searchProveedores(DTO_SearchProveedor searchDTO, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 1. Buscar por ID (prefijo)
        if (searchDTO.getSearchType() == DTO_SearchProveedor.SearchType.ID && searchDTO.getId() != null) {
            return proveedorRepo.findByIdStartingWith(searchDTO.getId(), pageable);
        }

        // 2. Buscar por nombre y categoría (en base de datos)
        if (searchDTO.getSearchType() == DTO_SearchProveedor.SearchType.NOMBRE_Y_CATEGORIA) {
            String nombre = (searchDTO.getNombre() != null && !searchDTO.getNombre().isEmpty())
                    ? searchDTO.getNombre()
                    : null;

            int[] categorias = (searchDTO.getCategorias() != null && searchDTO.getCategorias().length > 0)
                    ? searchDTO.getCategorias()
                    : null;

            return proveedorRepo.searchByNombreAndCategorias(nombre, categorias, pageable);
        }

        // 3. Caso por defecto: retornar página vacía
        return Page.empty(pageable);
    }



}
