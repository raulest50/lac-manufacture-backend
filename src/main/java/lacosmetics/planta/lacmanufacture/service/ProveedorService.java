package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Proveedor;
import lacosmetics.planta.lacmanufacture.repo.ProveedorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Transactional
    public Proveedor saveProveedor(Proveedor proveedor){
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


}
