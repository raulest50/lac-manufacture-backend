package lacosmetics.planta.lacmanufacture.service.activos.fijos;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.ItemOrdenCompraActivo;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.OrdenCompraActivo;
import lacosmetics.planta.lacmanufacture.repo.activos.fijos.ItemOrdenCompraActivoRepo;
import lacosmetics.planta.lacmanufacture.repo.activos.fijos.OrdenCompraActivoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de activos fijos.
 * Proporciona métodos para administrar órdenes de compra, incorporaciones,
 * depreciaciones y otros aspectos relacionados con los activos fijos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivoFijoService {

    private final OrdenCompraActivoRepo ordenCompraActivoRepo;
    private final ItemOrdenCompraActivoRepo itemOrdenCompraActivoRepo;

    /**
     * Guarda una nueva orden de compra de activos fijos.
     *
     * @param ordenCompraActivo la orden de compra a guardar
     * @return la orden de compra guardada con su ID asignado
     */
    @Transactional
    public OrdenCompraActivo saveOrdenCompraActivo(OrdenCompraActivo ordenCompraActivo) {
        log.info("Guardando orden de compra de activos fijos");

        // Validaciones básicas
        if (ordenCompraActivo.getProveedor() == null) {
            throw new IllegalArgumentException("La orden de compra debe tener un proveedor asignado");
        }

        if (ordenCompraActivo.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida");
        }

        // Establecer valores por defecto si no están presentes
        if (ordenCompraActivo.getEstado() == 0) {
            // Estado 0: pendiente liberación (por defecto)
            ordenCompraActivo.setEstado(0);
        }

        // Procesar los ítems de la orden
        if (ordenCompraActivo.getItemsOrdenCompra() != null && !ordenCompraActivo.getItemsOrdenCompra().isEmpty()) {
            double subtotal = 0;
            double ivaTotal = 0;

            for (var item : ordenCompraActivo.getItemsOrdenCompra()) {
                // Establecer la relación bidireccional
                item.setOrdenCompraActivo(ordenCompraActivo);

                // Calcular subtotal del ítem si no está establecido
                if (item.getSubTotal() == 0) {
                    item.setSubTotal(item.getPrecioUnitario() * item.getCantidad());
                }

                // Acumular totales
                subtotal += item.getSubTotal();
                ivaTotal += item.getIva() * item.getCantidad();
            }

            // Actualizar totales de la orden
            ordenCompraActivo.setSubTotal(subtotal);
            ordenCompraActivo.setIva(ivaTotal);
            ordenCompraActivo.setTotalPagar(subtotal + ivaTotal);
        } else {
            // Calcular totales si no hay ítems pero están establecidos manualmente
            if (ordenCompraActivo.getTotalPagar() == 0) {
                double subtotal = ordenCompraActivo.getSubTotal();
                double iva = ordenCompraActivo.getIva();
                ordenCompraActivo.setTotalPagar(subtotal + iva);
            }
        }

        return ordenCompraActivoRepo.save(ordenCompraActivo);
    }

    /**
     * Busca órdenes de compra por rango de fechas y estados.
     *
     * @param date1 fecha inicial en formato yyyy-MM-dd
     * @param date2 fecha final en formato yyyy-MM-dd
     * @param estados cadena de estados separados por coma (ej: "0,1,2")
     * @param page número de página (0-indexed)
     * @param size tamaño de página
     * @return página de órdenes de compra que cumplen con los criterios
     */
    public Page<OrdenCompraActivo> getOrdenesCompraByDateAndEstado(
            String date1, String date2, String estados, int page, int size) {

        // Convertir fechas de String a LocalDateTime
        LocalDateTime startDate = LocalDate.parse(date1).atTime(LocalTime.MIN);
        LocalDateTime endDate = LocalDate.parse(date2).atTime(LocalTime.MAX);

        // Convertir estados de String a List<Integer>
        List<Integer> estadosList = Arrays.stream(estados.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);

        return ordenCompraActivoRepo.findByFechaEmisionBetweenAndEstadoIn(
                startDate, endDate, estadosList, pageable);
    }

    /**
     * Obtiene una orden de compra por su ID y estado.
     *
     * @param ordenCompraActivoId ID de la orden de compra
     * @param estado estado de la orden
     * @return la orden de compra si existe
     * @throws RuntimeException si no se encuentra la orden
     */
    public OrdenCompraActivo getOrdenCompraByIdAndEstado(Integer ordenCompraActivoId, int estado) {
        return ordenCompraActivoRepo.findByOrdenCompraActivoIdAndEstado(ordenCompraActivoId, estado)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada con ID: " + 
                        ordenCompraActivoId + " y estado: " + estado));
    }

    /**
     * Cancela una orden de compra de activos fijos.
     *
     * @param ordenCompraActivoId ID de la orden de compra a cancelar
     * @return la orden de compra actualizada
     * @throws RuntimeException si no se encuentra la orden
     */
    @Transactional
    public OrdenCompraActivo cancelOrdenCompraActivo(int ordenCompraActivoId) {
        OrdenCompraActivo ordenCompra = ordenCompraActivoRepo.findById(ordenCompraActivoId)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada con ID: " + ordenCompraActivoId));

        // Verificar si la orden ya está cancelada
        if (ordenCompra.getEstado() == -1) {
            throw new RuntimeException("La orden de compra ya está cancelada");
        }

        // Verificar si la orden está en un estado que permite cancelación
        if (ordenCompra.getEstado() > 1) {
            throw new RuntimeException("No se puede cancelar una orden que ya está en proceso de envío o recepción");
        }

        // Cancelar la orden
        ordenCompra.setEstado(-1);
        return ordenCompraActivoRepo.save(ordenCompra);
    }

    /**
     * Obtiene los ítems de una orden de compra específica.
     *
     * @param ordenCompraActivoId ID de la orden de compra
     * @return lista de ítems de la orden
     */
    public List<ItemOrdenCompraActivo> getItemsByOrdenCompraId(int ordenCompraActivoId) {
        return itemOrdenCompraActivoRepo.findByOrdenCompraActivo_OrdenCompraActivoId(ordenCompraActivoId);
    }
}
