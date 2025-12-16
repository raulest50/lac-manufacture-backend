package lacosmetics.planta.lacmanufacture.service.inventarios;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.SearchOCMFilterDTO;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class IngresoAlmacenService {

    private final OrdenCompraRepo ordenCompraRepo;
    private final ProveedorRepo proveedorRepo;

    private static final int ESTADO_PENDIENTE_INGRESO_ALMACEN = 2;

    public Page<OrdenCompraMateriales> consultarOCMsPendientesRecepcion(
            SearchOCMFilterDTO filterDTO,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEmision").descending());

        LocalDateTime fechaInicio = filterDTO.getFechaInicio();
        LocalDateTime fechaFin = filterDTO.getFechaFin();
        String proveedorId = filterDTO.getProveedorId();

        // If both dates and proveedor are provided
        if (fechaInicio != null && fechaFin != null && proveedorId != null && !proveedorId.trim().isEmpty()) {
            // Load Proveedor entity by business id (String)
            Proveedor proveedor = proveedorRepo.findById(proveedorId)
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con id: " + proveedorId));
            
            return ordenCompraRepo.findByEstadoAndProveedorAndFechaEmisionBetween(
                    ESTADO_PENDIENTE_INGRESO_ALMACEN,
                    proveedor,
                    fechaInicio,
                    fechaFin,
                    pageable
            );
        }
        // If only dates are provided
        else if (fechaInicio != null && fechaFin != null) {
            return ordenCompraRepo.findByFechaEmisionBetweenAndEstadoIn(
                    fechaInicio,
                    fechaFin,
                    Collections.singletonList(ESTADO_PENDIENTE_INGRESO_ALMACEN),
                    pageable
            );
        }
        // If only proveedor is provided
        else if (proveedorId != null && !proveedorId.trim().isEmpty()) {
            return ordenCompraRepo.findByProveedorIdAndEstado(
                    proveedorId,
                    ESTADO_PENDIENTE_INGRESO_ALMACEN,
                    pageable
            );
        }
        // If neither is provided (or dates are null)
        else {
            return ordenCompraRepo.findByEstado(
                    ESTADO_PENDIENTE_INGRESO_ALMACEN,
                    pageable
            );
        }
    }

}
