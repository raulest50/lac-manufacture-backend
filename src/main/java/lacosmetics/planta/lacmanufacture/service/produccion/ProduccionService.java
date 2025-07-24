package lacosmetics.planta.lacmanufacture.service.produccion;


import org.springframework.transaction.annotation.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.dto.InventarioEnTransitoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO_save;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenSeguimientoDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProduccionService {


    private final OrdenProduccionRepo ordenProduccionRepo;
    private final TerminadoRepo terminadoRepo;
    private final TransaccionAlmacenRepo movmientoRepo;

    private final ProductoRepo productoRepo;

    @Autowired
    private final OrdenSeguimientoRepo ordenSeguimientoRepo;

    @Autowired
    private TransaccionAlmacenRepo transaccionAlmacenRepo;


    @Transactional(rollbackFor = Exception.class)
    public OrdenProduccion saveOrdenProduccion(OrdenProduccionDTO_save ordenProduccionDTO) {
        Optional<Producto> optionalProducto = productoRepo.findById(ordenProduccionDTO.getProductoId());
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            OrdenProduccion ordenProduccion = new OrdenProduccion(producto, ordenProduccionDTO.getObservaciones());
            OrdenProduccion savedOrden = ordenProduccionRepo.save(ordenProduccion);

            // Create Movimiento entries for each Insumo
            for (OrdenSeguimiento ordenSeguimiento : savedOrden.getOrdenesSeguimiento()) {
                Insumo insumo = ordenSeguimiento.getInsumo();
                Movimiento movimientoReal = new Movimiento();
                movimientoReal.setCantidad(-insumo.getCantidadRequerida()); // Negative cantidad
                movimientoReal.setProducto(insumo.getProducto());
                movimientoReal.setTipoMovimiento(Movimiento.TipoMovimiento.CONSUMO);
                //movimiento.setObservaciones("Consumo para Orden de Producción ID: " + savedOrden.getOrdenId());
                transaccionAlmacenRepo.save(movimientoReal);
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




    public Page<InventarioEnTransitoDTO> getInventarioEnTransito(Pageable pageable) {
        // Fetch all Ordenes de Producción with estadoOrden = 0
        List<OrdenProduccion> ordenesProduccion = ordenProduccionRepo.findByEstadoOrden(0);

        // Initialize necessary associations
        for (OrdenProduccion orden : ordenesProduccion) {
            Hibernate.initialize(orden.getOrdenesSeguimiento());
            for (OrdenSeguimiento seguimiento : orden.getOrdenesSeguimiento()) {
                Hibernate.initialize(seguimiento.getInsumo());
                Hibernate.initialize(seguimiento.getInsumo().getProducto());
            }
        }

        // Map to hold Producto ID and corresponding InventarioEnTransitoDTO
        Map<String, InventarioEnTransitoDTO> inventarioMap = new HashMap<>();

        // Process each Orden de Producción
        for (OrdenProduccion orden : ordenesProduccion) {
            int ordenProduccionId = orden.getOrdenId();
            for (OrdenSeguimiento seguimiento : orden.getOrdenesSeguimiento()) {
                Insumo insumo = seguimiento.getInsumo();
                Producto producto = insumo.getProducto();
                String productoId = producto.getProductoId();
                String productoNombre = producto.getNombre();
                double cantidadRequerida = insumo.getCantidadRequerida();

                InventarioEnTransitoDTO inventarioDTO = inventarioMap.get(productoId);
                if (inventarioDTO == null) {
                    inventarioDTO = new InventarioEnTransitoDTO();
                    inventarioDTO.setProductoId(productoId);
                    inventarioDTO.setProductoNombre(productoNombre);
                    inventarioDTO.setCantidadTotal(cantidadRequerida);
                    inventarioDTO.setOrdenesProduccionIds(new ArrayList<>());
                    inventarioDTO.getOrdenesProduccionIds().add(ordenProduccionId);
                    inventarioMap.put(productoId, inventarioDTO);
                } else {
                    inventarioDTO.setCantidadTotal(inventarioDTO.getCantidadTotal() + cantidadRequerida);
                    if (!inventarioDTO.getOrdenesProduccionIds().contains(ordenProduccionId)) {
                        inventarioDTO.getOrdenesProduccionIds().add(ordenProduccionId);
                    }
                }
            }
        }

        // Convert map values to a list
        List<InventarioEnTransitoDTO> inventarioList = new ArrayList<>(inventarioMap.values());

        // Apply sorting if needed (e.g., by productoNombre)
        inventarioList.sort(Comparator.comparing(InventarioEnTransitoDTO::getProductoNombre));

        // Implement pagination manually
        int total = inventarioList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<InventarioEnTransitoDTO> output;
        if (start <= end) {
            output = inventarioList.subList(start, end);
        } else {
            output = new ArrayList<>();
        }

        return new PageImpl<>(output, pageable, total);
    }



    /**
     * Update the estado of an OrdenSeguimiento.
     */
    @Transactional
    public OrdenSeguimientoDTO updateEstadoOrdenSeguimiento(int seguimientoId, int estado) {
        ordenSeguimientoRepo.updateEstadoById(seguimientoId, estado);

        // Fetch updated OrdenSeguimiento
        OrdenSeguimiento ordenSeguimiento = ordenSeguimientoRepo.findById(seguimientoId).orElseThrow(() -> new RuntimeException("OrdenSeguimiento not found"));

        // Return updated DTO
        return convertSeguimientoToDto(ordenSeguimiento);
    }

    /**
     * Update the estadoOrden of an OrdenProduccion and register Movimiento.
     */
    @Transactional
    public OrdenProduccionDTO updateEstadoOrdenProduccion(int ordenId, int estadoOrden) {
        ordenProduccionRepo.updateEstadoOrdenById(ordenId, estadoOrden);

        // Fetch updated OrdenProduccion
        OrdenProduccion ordenProduccion = ordenProduccionRepo.findById(ordenId).orElseThrow(() -> new RuntimeException("OrdenProduccion not found"));

        // Register Movimiento for the produced Producto
        Movimiento movimientoReal = new Movimiento();
        movimientoReal.setCantidad(ordenProduccion.getProducto().getCantidadUnidad()); // Adjust as per your business logic
        movimientoReal.setProducto(ordenProduccion.getProducto());
        movimientoReal.setTipoMovimiento(Movimiento.TipoMovimiento.BACKFLUSH);
        //movimiento.setObservaciones("Producción finalizada para Orden ID: " + ordenId);
        transaccionAlmacenRepo.save(movimientoReal);

        return convertToDto(ordenProduccion);
    }






}
