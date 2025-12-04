package lacosmetics.planta.lacmanufacture.service.productos;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.InsumoRepo;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta.Insumo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemiTerService {

    private final ProductoRepo productoRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;
    private final InsumoRepo insumoRepo;
    private final TransaccionAlmacenRepo transaccionAlmacenRepo;
    private final OrdenProduccionRepo ordenProduccionRepo;

    /**
     * Verifica si un Semiterminado o Terminado puede ser eliminado.
     * Un producto es eliminable si:
     * 1. Su stock es 0
     * 2. No existe ninguna orden de producción abierta que lo relacione
     *
     * @param productoId ID del producto a verificar
     * @return Un objeto con información sobre si el producto es eliminable y la razón si no lo es
     */
    @Transactional(readOnly = true)
    public Map<String, Object> isProductoDeletable(String productoId) {
        log.info("Verificando si el producto con ID: {} es eliminable", productoId);
        Map<String, Object> result = new HashMap<>();

        // Verificar que el producto existe y es un Semiterminado o Terminado
        Optional<Producto> optProducto = productoRepo.findById(productoId);
        if (optProducto.isEmpty()) {
            result.put("deletable", false);
            result.put("reason", "No se encontró el producto con ID: " + productoId);
            return result;
        }

        Producto producto = optProducto.get();
        if (!(producto instanceof SemiTerminado) && !(producto instanceof Terminado)) {
            result.put("deletable", false);
            result.put("reason", "El producto con ID: " + productoId + " no es un Semiterminado ni un Terminado");
            return result;
        }

        // Verificar que el stock es 0
        Double stock = transaccionAlmacenRepo.findTotalCantidadByProductoId(productoId);
        stock = (stock != null) ? stock : 0.0;

        if (stock > 0) {
            result.put("deletable", false);
            result.put("reason", "El producto tiene stock disponible: " + stock);
            result.put("stock", stock);
            return result;
        }

        // Verificar que no existen órdenes de producción abiertas que lo relacionen
        long openOrders = ordenProduccionRepo.countByProducto_ProductoIdAndEstadoOrden(productoId, 0);
        if (openOrders > 0) {
            result.put("deletable", false);
            result.put("reason", "El producto está relacionado con " + openOrders + " orden(es) de producción abierta(s)");
            result.put("openOrdersCount", openOrders);
            return result;
        }

        // Si no hay problemas, el producto es eliminable
        result.put("deletable", true);
        return result;
    }

    /**
     * Elimina un Semiterminado o Terminado de forma provisional.
     * Este método solo debe usarse en fases iniciales de adopción de la aplicación
     * para eliminar productos de prueba o con errores.
     * 
     * @param productoId ID del producto a eliminar
     * @return Un objeto con información sobre el resultado de la operación
     * @throws IllegalStateException Si el producto no es eliminable
     */
    @Transactional
    public Map<String, Object> deleteProductoProvisional(String productoId) {
        log.info("Eliminando provisionalmente el producto con ID: {}", productoId);

        // Verificar si el producto es eliminable
        Map<String, Object> checkResult = isProductoDeletable(productoId);
        if (!(boolean)checkResult.get("deletable")) {
            log.warn("No se puede eliminar el producto con ID {}: {}", productoId, checkResult.get("reason"));
            throw new IllegalStateException(checkResult.get("reason").toString());
        }

        // Obtener el producto
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con ID: " + productoId));

        // Eliminar órdenes de producción relacionadas (incluso las cerradas)
        List<OrdenProduccion> ordenes = ordenProduccionRepo.findByProducto_ProductoId(productoId);
        if (!ordenes.isEmpty()) {
            log.info("Eliminando {} órdenes de producción relacionadas con el producto {}", ordenes.size(), productoId);
            ordenProduccionRepo.deleteAll(ordenes);
        }

        // Eliminar el producto según su tipo
        if (producto instanceof SemiTerminado) {
            log.info("Eliminando SemiTerminado con ID: {}", productoId);
            semiTerminadoRepo.deleteById(productoId);
        } else if (producto instanceof Terminado) {
            log.info("Eliminando Terminado con ID: {}", productoId);
            terminadoRepo.deleteById(productoId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Producto eliminado correctamente");
        return result;
    }

    /**
     * Fuerza la eliminación de un Semiterminado o Terminado junto con sus dependencias directas
     * para evitar bloqueos por integridad referencial.
     *
     * @param productoId ID del producto a eliminar
     * @return Mapa con el resultado de la operación y conteos de entidades eliminadas
     */
    @Transactional
    public Map<String, Object> forceDeleteProducto(String productoId) {
        log.info("Forzando eliminación del producto con ID: {}", productoId);

        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con ID: " + productoId));

        if (!(producto instanceof SemiTerminado) && !(producto instanceof Terminado)) {
            throw new IllegalStateException("Solo se pueden eliminar productos Semiterminados o Terminados");
        }

        Map<String, Object> result = new HashMap<>();

        List<Movimiento> movimientos = transaccionAlmacenRepo.findByProducto_ProductoId(productoId);
        int deletedMovimientos = movimientos.size();
        if (!movimientos.isEmpty()) {
            log.info("Eliminando {} movimientos de inventario asociados al producto {}", deletedMovimientos, productoId);
            transaccionAlmacenRepo.deleteAll(movimientos);
        }

        List<OrdenProduccion> ordenes = ordenProduccionRepo.findByProducto_ProductoId(productoId);
        int deletedOrdenes = ordenes.size();
        if (!ordenes.isEmpty()) {
            log.info("Eliminando {} órdenes de producción asociadas al producto {}", deletedOrdenes, productoId);
            ordenProduccionRepo.deleteAll(ordenes);
        }

        List<Insumo> insumos = insumoRepo.findByProducto_ProductoId(productoId);
        int deletedInsumos = insumos.size();
        if (!insumos.isEmpty()) {
            log.info("Eliminando {} insumos que referencian al producto {}", deletedInsumos, productoId);
            insumoRepo.deleteAll(insumos);
        }

        if (producto instanceof SemiTerminado) {
            log.info("Eliminando Semiterminado con ID: {}", productoId);
            semiTerminadoRepo.deleteById(productoId);
        } else {
            log.info("Eliminando Terminado con ID: {}", productoId);
            terminadoRepo.deleteById(productoId);
        }

        result.put("success", true);
        result.put("message", "Producto eliminado correctamente");
        result.put("deletedMovimientos", deletedMovimientos);
        result.put("deletedOrdenesProduccion", deletedOrdenes);
        result.put("deletedInsumos", deletedInsumos);

        return result;
    }
}
