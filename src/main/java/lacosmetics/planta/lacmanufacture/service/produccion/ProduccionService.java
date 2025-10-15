package lacosmetics.planta.lacmanufacture.service.produccion;


import org.springframework.transaction.annotation.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.inventarios.Lote;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.dto.DispensacionDTO;
import lacosmetics.planta.lacmanufacture.model.dto.DispensacionFormularioDTO;
import lacosmetics.planta.lacmanufacture.model.dto.InventarioEnTransitoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.InsumoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.LoteRecomendadoDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO_save;
import lacosmetics.planta.lacmanufacture.model.dto.OrdenSeguimientoDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.repo.inventarios.LoteRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.service.contabilidad.ContabilidadService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProduccionService {


    private final OrdenProduccionRepo ordenProduccionRepo;
    private final TerminadoRepo terminadoRepo;
    private final TransaccionAlmacenRepo movmientoRepo;
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;
    private final ContabilidadService contabilidadService;

    private final ProductoRepo productoRepo;

    private final LoteRepo loteRepo;

    @Autowired
    private final OrdenSeguimientoRepo ordenSeguimientoRepo;

    @Autowired
    private TransaccionAlmacenRepo transaccionAlmacenRepo;


    @Transactional(rollbackFor = Exception.class)
    public OrdenProduccion saveOrdenProduccion(OrdenProduccionDTO_save ordenProduccionDTO) {
        Optional<Producto> optionalProducto = productoRepo.findById(ordenProduccionDTO.getProductoId());
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            OrdenProduccion ordenProduccion = new OrdenProduccion(producto, ordenProduccionDTO.getObservaciones(), ordenProduccionDTO.getNumeroLotes());
            ordenProduccion.setFechaLanzamiento(ordenProduccionDTO.getFechaLanzamiento());
            ordenProduccion.setFechaFinalPlanificada(ordenProduccionDTO.getFechaFinalPlanificada());
            ordenProduccion.setNumeroPedidoComercial(ordenProduccionDTO.getNumeroPedidoComercial());
            ordenProduccion.setAreaOperativa(ordenProduccionDTO.getAreaOperativa());
            ordenProduccion.setDepartamentoOperativo(ordenProduccionDTO.getDepartamentoOperativo());
            OrdenProduccion savedOrden = ordenProduccionRepo.save(ordenProduccion);

            if (ordenProduccionDTO.getLoteBatchNumber() != null && !ordenProduccionDTO.getLoteBatchNumber().isBlank()) {
                Lote lote = new Lote();
                lote.setBatchNumber(ordenProduccionDTO.getLoteBatchNumber());
                lote.setOrdenProduccion(savedOrden);
                loteRepo.save(lote);
            }

            List<OrdenSeguimiento> ordenesSeguimiento = savedOrden.getOrdenesSeguimiento();
            if (ordenesSeguimiento != null && !ordenesSeguimiento.isEmpty()) {
                // Create Movimiento entries for each Insumo
                for (OrdenSeguimiento ordenSeguimiento : ordenesSeguimiento) {
                    Insumo insumo = ordenSeguimiento.getInsumo();
                    Movimiento movimientoReal = new Movimiento();
                    movimientoReal.setCantidad(-insumo.getCantidadRequerida()); // Negative cantidad
                    movimientoReal.setProducto(insumo.getProducto());
                    movimientoReal.setTipoMovimiento(Movimiento.TipoMovimiento.CONSUMO);
                    //movimiento.setObservaciones("Consumo para Orden de Producción ID: " + savedOrden.getOrdenId());
                    transaccionAlmacenRepo.save(movimientoReal);
                }
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
        Page<OrdenProduccion> page = ordenProduccionRepo.findByFechaCreacionBetweenAndEstadoOrden(startDate, endDate, estadoOrden, pageable);
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
        dto.setFechaCreacion(orden.getFechaCreacion());
        dto.setFechaLanzamiento(orden.getFechaLanzamiento());
        dto.setFechaFinalPlanificada(orden.getFechaFinalPlanificada());
        dto.setEstadoOrden(orden.getEstadoOrden());
        dto.setObservaciones(orden.getObservaciones());
        dto.setNumeroLotes(orden.getNumeroLotes());
        dto.setNumeroPedidoComercial(orden.getNumeroPedidoComercial());
        dto.setAreaOperativa(orden.getAreaOperativa());
        dto.setDepartamentoOperativo(orden.getDepartamentoOperativo());

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
     * For completed production orders, also creates an accounting entry.
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
        movimientoReal.setAlmacen(Movimiento.Almacen.GENERAL);

        // Create a transaction for this movement
        TransaccionAlmacen transaccion = new TransaccionAlmacen();
        transaccion.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OP);
        transaccion.setIdEntidadCausante(ordenId);
        transaccion.setObservaciones("Producción finalizada para Orden ID: " + ordenId);

        // Add the movement to the transaction
        List<Movimiento> movimientos = new ArrayList<>();
        movimientos.add(movimientoReal);
        transaccion.setMovimientosTransaccion(movimientos);
        movimientoReal.setTransaccionAlmacen(transaccion);

        // Save the transaction
        transaccionAlmacenHeaderRepo.save(transaccion);

        // Create accounting entry for BACKFLUSH
        try {
            // Calculate the total amount based on the product cost
            BigDecimal montoTotal = BigDecimal.valueOf(ordenProduccion.getProducto().getCosto() * 
                                                      ordenProduccion.getProducto().getCantidadUnidad());

            // Register the accounting entry
            AsientoContable asiento = contabilidadService.registrarAsientoBackflush(transaccion, ordenProduccion, montoTotal);

            // Update the transaction with the accounting entry reference
            transaccion.setAsientoContable(asiento);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.CONTABILIZADA);
            transaccionAlmacenHeaderRepo.save(transaccion);

            log.info("Asiento contable registrado con ID: " + asiento.getId() + " para la OP: " + ordenId);
        } catch (Exception e) {
            log.error("Error al registrar asiento contable para OP " + ordenId + ": " + e.getMessage(), e);
            // We don't interrupt the main flow if accounting fails
        }

        return convertToDto(ordenProduccion);
    }

    /**
     * Get the inputs (insumos) needed for a production order.
     * Includes recommended batches for each input, prioritizing those with closest expiration date.
     */
    public List<InsumoDTO> getInsumosOrdenProduccion(int ordenId) {
        OrdenProduccion ordenProduccion = ordenProduccionRepo.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden de producción no encontrada con ID: " + ordenId));

        return ordenProduccion.getOrdenesSeguimiento().stream()
            .map(ordenSeguimiento -> {
                Insumo insumo = ordenSeguimiento.getInsumo();
                InsumoDTO insumoDTO = new InsumoDTO();
                insumoDTO.setProductoId(insumo.getProducto().getProductoId());
                insumoDTO.setNombreProducto(insumo.getProducto().getNombre());
                insumoDTO.setCantidadRequerida(insumo.getCantidadRequerida());
                insumoDTO.setEstadoSeguimiento(ordenSeguimiento.getEstado());
                insumoDTO.setSeguimientoId(ordenSeguimiento.getSeguimientoId());
                insumoDTO.setLotesRecomendados(new ArrayList<>());

                // Obtener lotes con stock disponible para este producto
                List<Object[]> lotesConStock;
                try {
                    // Intentar primero con la consulta JPQL
                    lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdOrderByExpirationDate(
                        insumo.getProducto().getProductoId());
                } catch (Exception e) {
                    // Si falla, usar la consulta SQL nativa como alternativa
                    log.warn("Error al ejecutar consulta JPQL para lotes, usando SQL nativo: " + e.getMessage());
                    lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdNative(
                        insumo.getProducto().getProductoId());
                }

                // Mapear a DTOs y calcular cuánto tomar de cada lote
                List<LoteRecomendadoDTO> lotesRecomendados = new ArrayList<>();
                double cantidadRestante = insumo.getCantidadRequerida();

                for (Object[] result : lotesConStock) {
                    if (cantidadRestante <= 0) {
                        break;
                    }

                    try {
                        Lote lote;
                        Double stockDisponible;

                        // Intentar procesar como resultado de consulta JPQL
                        if (result[0] instanceof Lote) {
                            lote = (Lote) result[0];
                            stockDisponible = (Double) result[1];
                        } 
                        // Procesar como resultado de consulta SQL nativa
                        else {
                            // Para SQL nativo, necesitamos buscar el lote por ID
                            Long loteId = null;

                            // El primer elemento podría ser un número (ID del lote)
                            if (result[0] instanceof Number) {
                                loteId = ((Number) result[0]).longValue();
                            } 
                            // O podría ser un mapa con los valores de las columnas
                            else if (result[0] instanceof Map) {
                                Map<String, Object> map = (Map<String, Object>) result[0];
                                loteId = ((Number) map.get("id")).longValue();
                            }

                            // Si no pudimos obtener el ID del lote, continuamos con el siguiente
                            if (loteId == null) {
                                log.warn("No se pudo obtener el ID del lote del resultado de la consulta");
                                continue;
                            }

                            // Buscar el lote por ID
                            Optional<Lote> optionalLote = loteRepo.findById(loteId);
                            if (!optionalLote.isPresent()) {
                                log.warn("No se encontró el lote con ID: " + loteId);
                                continue;
                            }

                            lote = optionalLote.get();

                            // El stock disponible podría estar en diferentes posiciones según la consulta
                            if (result.length > 1 && result[1] instanceof Number) {
                                stockDisponible = ((Number) result[1]).doubleValue();
                            } else {
                                log.warn("No se pudo obtener el stock disponible del resultado de la consulta");
                                continue;
                            }
                        }

                        double cantidadAUsar = Math.min(stockDisponible, cantidadRestante);

                        LoteRecomendadoDTO loteRecomendado = new LoteRecomendadoDTO();
                        loteRecomendado.setLoteId(lote.getId());
                        loteRecomendado.setBatchNumber(lote.getBatchNumber());
                        loteRecomendado.setProductionDate(lote.getProductionDate());
                        loteRecomendado.setExpirationDate(lote.getExpirationDate());
                        loteRecomendado.setCantidadDisponible(stockDisponible);  // Cantidad total disponible en el lote
                        loteRecomendado.setCantidadRecomendada(cantidadAUsar);   // Cantidad recomendada a tomar

                        lotesRecomendados.add(loteRecomendado);

                        cantidadRestante -= cantidadAUsar;
                    } catch (Exception e) {
                        log.error("Error al procesar lote recomendado: " + e.getMessage(), e);
                        // Continuamos con el siguiente lote
                    }
                }

                insumoDTO.setLotesRecomendados(lotesRecomendados);

                return insumoDTO;
            })
            .collect(Collectors.toList());
    }
    /**
     * Obtiene un formulario de dispensación para una orden de producción.
     * Incluye todos los materiales necesarios y los lotes recomendados para cada uno,
     * priorizando los lotes más próximos a vencer.
     * 
     * @param ordenId ID de la orden de producción
     * @return DTO con la información del formulario de dispensación
     */
    public DispensacionFormularioDTO getFormularioDispensacion(int ordenId) {
        OrdenProduccion ordenProduccion = ordenProduccionRepo.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden de producción no encontrada con ID: " + ordenId));

        // Crear el DTO del formulario
        DispensacionFormularioDTO formulario = new DispensacionFormularioDTO();
        formulario.setOrdenProduccionId(ordenId);
        formulario.setProductoNombre(ordenProduccion.getProducto().getNombre());

        // Obtener las dispensaciones para cada insumo
        List<DispensacionDTO> dispensaciones = ordenProduccion.getOrdenesSeguimiento().stream()
            .map(ordenSeguimiento -> {
                Insumo insumo = ordenSeguimiento.getInsumo();
                DispensacionDTO dispensacionDTO = new DispensacionDTO();
                dispensacionDTO.setProductoId(insumo.getProducto().getProductoId());
                dispensacionDTO.setNombreProducto(insumo.getProducto().getNombre());
                dispensacionDTO.setCantidadRequerida(insumo.getCantidadRequerida());
                dispensacionDTO.setEstadoSeguimiento(ordenSeguimiento.getEstado());
                dispensacionDTO.setSeguimientoId(ordenSeguimiento.getSeguimientoId());
                dispensacionDTO.setLotesRecomendados(new ArrayList<>());

                // Obtener lotes con stock disponible para este producto
                List<Object[]> lotesConStock;
                try {
                    // Intentar primero con la consulta JPQL
                    lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdOrderByExpirationDate(
                        insumo.getProducto().getProductoId());
                } catch (Exception e) {
                    // Si falla, usar la consulta SQL nativa como alternativa
                    log.warn("Error al ejecutar consulta JPQL para lotes, usando SQL nativo: " + e.getMessage());
                    lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdNative(
                        insumo.getProducto().getProductoId());
                }

                // Mapear a DTOs y calcular cuánto tomar de cada lote
                List<LoteRecomendadoDTO> lotesRecomendados = new ArrayList<>();
                double cantidadRestante = insumo.getCantidadRequerida();

                for (Object[] result : lotesConStock) {
                    if (cantidadRestante <= 0) {
                        break;
                    }

                    try {
                        Lote lote;
                        Double stockDisponible;

                        // Intentar procesar como resultado de consulta JPQL
                        if (result[0] instanceof Lote) {
                            lote = (Lote) result[0];
                            stockDisponible = (Double) result[1];
                        } 
                        // Procesar como resultado de consulta SQL nativa
                        else {
                            // Para SQL nativo, necesitamos buscar el lote por ID
                            Long loteId = null;

                            // El primer elemento podría ser un número (ID del lote)
                            if (result[0] instanceof Number) {
                                loteId = ((Number) result[0]).longValue();
                            } 
                            // O podría ser un mapa con los valores de las columnas
                            else if (result[0] instanceof Map) {
                                Map<String, Object> map = (Map<String, Object>) result[0];
                                loteId = ((Number) map.get("id")).longValue();
                            }

                            // Si no pudimos obtener el ID del lote, continuamos con el siguiente
                            if (loteId == null) {
                                log.warn("No se pudo obtener el ID del lote del resultado de la consulta");
                                continue;
                            }

                            // Buscar el lote por ID
                            Optional<Lote> optionalLote = loteRepo.findById(loteId);
                            if (!optionalLote.isPresent()) {
                                log.warn("No se encontró el lote con ID: " + loteId);
                                continue;
                            }

                            lote = optionalLote.get();

                            // El stock disponible podría estar en diferentes posiciones según la consulta
                            if (result.length > 1 && result[1] instanceof Number) {
                                stockDisponible = ((Number) result[1]).doubleValue();
                            } else {
                                log.warn("No se pudo obtener el stock disponible del resultado de la consulta");
                                continue;
                            }
                        }

                        double cantidadAUsar = Math.min(stockDisponible, cantidadRestante);

                        LoteRecomendadoDTO loteRecomendado = new LoteRecomendadoDTO();
                        loteRecomendado.setLoteId(lote.getId());
                        loteRecomendado.setBatchNumber(lote.getBatchNumber());
                        loteRecomendado.setProductionDate(lote.getProductionDate());
                        loteRecomendado.setExpirationDate(lote.getExpirationDate());
                        loteRecomendado.setCantidadDisponible(stockDisponible);  // Cantidad total disponible en el lote
                        loteRecomendado.setCantidadRecomendada(cantidadAUsar);   // Cantidad recomendada a tomar

                        lotesRecomendados.add(loteRecomendado);

                        cantidadRestante -= cantidadAUsar;
                    } catch (Exception e) {
                        log.error("Error al procesar lote recomendado: " + e.getMessage(), e);
                        // Continuamos con el siguiente lote
                    }
                }

                dispensacionDTO.setLotesRecomendados(lotesRecomendados);

                return dispensacionDTO;
            })
            .collect(Collectors.toList());

        formulario.setDispensaciones(dispensaciones);

        return formulario;
    }
}
