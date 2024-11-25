package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Insumo;
import lacosmetics.planta.lacmanufacture.model.Movimiento;
import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO_save;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenSeguimientoDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.repo.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenSeguimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProduccionService {


    private final OrdenProduccionRepo ordenProduccionRepo;
    private final TerminadoRepo terminadoRepo;
    private final MovimientoRepo movmientoRepo;

    private final ProductoRepo productoRepo;

    @Autowired
    private final OrdenSeguimientoRepo ordenSeguimientoRepo;

    @Autowired
    private MovimientoRepo movimientoRepo;


    @Transactional(rollbackOn = Exception.class)
    public OrdenProduccion saveOrdenProduccion(OrdenProduccionDTO_save ordenProduccionDTO) {
        Optional<Producto> optionalProducto = productoRepo.findById(ordenProduccionDTO.getProductoId());
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            OrdenProduccion ordenProduccion = new OrdenProduccion(producto, ordenProduccionDTO.getObservaciones(), ordenProduccionDTO.getResponsableId());
            OrdenProduccion savedOrden = ordenProduccionRepo.save(ordenProduccion);

            // Create Movimiento entries for each Insumo
            for (OrdenSeguimiento ordenSeguimiento : savedOrden.getOrdenesSeguimiento()) {
                Insumo insumo = ordenSeguimiento.getInsumo();
                Movimiento movimiento = new Movimiento();
                movimiento.setCantidad(-insumo.getCantidadRequerida()); // Negative cantidad
                movimiento.setProducto(insumo.getProducto());
                movimiento.setCausa(Movimiento.CausaMovimiento.USO_INTERNO);
                movimiento.setObservaciones("Consumo para Orden de Producción ID: " + savedOrden.getOrdenId());
                movimientoRepo.save(movimiento);
            }

            return savedOrden;
        } else {
            throw new RuntimeException("Producto not found");
        }
    }



    public Page<OrdenProduccionDTO> searchOrdenesProduccionByDateRangeAndEstadoOrden(
            LocalDateTime startDate,
            LocalDateTime endDate,
            int estadoOrden,
            Pageable pageable
    ) {
        Page<OrdenProduccion> page = ordenProduccionRepo.findByFechaInicioBetweenAndEstadoOrden(startDate, endDate, estadoOrden, pageable);
        // Initialize necessary associations
        page.getContent().forEach(orden -> {
            Hibernate.initialize(orden.getOrdenesSeguimiento());
            Hibernate.initialize(orden.getProducto());
        });

        // Map entities to DTOs
        List<OrdenProduccionDTO> dtoList = page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Helper method to map OrdenProduccion to OrdenProduccionDTO
    private OrdenProduccionDTO convertToDto(OrdenProduccion orden) {
        OrdenProduccionDTO dto = new OrdenProduccionDTO();
        dto.setOrdenId(orden.getOrdenId());
        dto.setProductoNombre(orden.getProducto().getNombre());
        dto.setFechaInicio(orden.getFechaInicio());
        dto.setEstadoOrden(orden.getEstadoOrden());
        dto.setResponsableId(orden.getResponsableId());
        dto.setObservaciones(orden.getObservaciones());

        List<OrdenSeguimientoDTO> seguimientoDTOs = orden.getOrdenesSeguimiento().stream()
                .map(this::convertSeguimientoToDto)
                .collect(Collectors.toList());
        dto.setOrdenesSeguimiento(seguimientoDTOs);

        return dto;
    }

    // Helper method to map OrdenSeguimiento to OrdenSeguimientoDTO
    private OrdenSeguimientoDTO convertSeguimientoToDto(OrdenSeguimiento seguimiento) {
        OrdenSeguimientoDTO dto = new OrdenSeguimientoDTO();
        dto.setSeguimientoId(seguimiento.getSeguimientoId());
        dto.setInsumoNombre(seguimiento.getInsumo().getProducto().getNombre());
        dto.setCantidadRequerida(seguimiento.getInsumo().getCantidadRequerida());
        dto.setEstado(seguimiento.getEstado());
        return dto;
    }


}
