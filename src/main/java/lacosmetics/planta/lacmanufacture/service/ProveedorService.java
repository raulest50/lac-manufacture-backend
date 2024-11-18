package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Proveedor;
import lacosmetics.planta.lacmanufacture.repo.ProveedorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

}
