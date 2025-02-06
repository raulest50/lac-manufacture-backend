package lacosmetics.planta.lacmanufacture.service;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.*;
import lacosmetics.planta.lacmanufacture.model.compras.Compra;
import lacosmetics.planta.lacmanufacture.model.compras.ItemCompra;
import lacosmetics.planta.lacmanufacture.model.compras.ItemOrdenCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.*;
import lacosmetics.planta.lacmanufacture.repo.compras.CompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MateriaPrimaRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ComprasService {

    private final CompraRepo compraRepo;
    private final MovimientoRepo movimientoRepo;
    private final ProveedorRepo proveedorRepo;
    private final MateriaPrimaRepo materiaPrimaRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;

    private final OrdenCompraRepo ordenCompraRepo;

    /**
     *
     * Compras
     *
     */

    @Transactional
    public Compra saveCompra(Compra compra) {
        // Verify that the Proveedor exists
        Optional<Proveedor> optionalProveedor = proveedorRepo.findById(compra.getProveedor().getId());
        if (!optionalProveedor.isPresent()) {
            throw new RuntimeException("Proveedor not found with ID: " + compra.getProveedor().getId());
        }
        compra.setProveedor(optionalProveedor.get());

        // For each ItemCompra, set the Compra reference, verify MateriaPrima, update costo ponderado, and cascade updates
        for (ItemCompra itemCompra : compra.getItemsCompra()) {
            itemCompra.setCompra(compra);

            // Verify that the MateriaPrima exists
            Optional<MateriaPrima> optionalMateriaPrima = materiaPrimaRepo.findById(itemCompra.getMateriaPrima().getProductoId());
            if (!optionalMateriaPrima.isPresent()) {
                throw new RuntimeException("MateriaPrima not found with ID: " + itemCompra.getMateriaPrima().getProductoId());
            }
            MateriaPrima materiaPrima = optionalMateriaPrima.get();
            itemCompra.setMateriaPrima(materiaPrima);

            // Retrieve current stock
            Double currentStockOpt = movimientoRepo.findTotalCantidadByProductoId(materiaPrima.getProductoId());
            int nuevoCosto = getNuevoCosto(itemCompra, currentStockOpt, materiaPrima);

            // Update MateriaPrima's costo
            materiaPrima.setCosto(nuevoCosto);

            // Save the updated MateriaPrima
            materiaPrimaRepo.save(materiaPrima);

            // Update costs of dependent products if necessary
            Set<Integer> updatedProductIds = new HashSet<>();
            updateCostoCascade(materiaPrima, updatedProductIds);
        }

        // Save the Compra (this will also save ItemCompra due to CascadeType.ALL)
        Compra savedCompra = compraRepo.save(compra);

        // Create Movimiento entries for each ItemCompra
        for (ItemCompra itemCompra : savedCompra.getItemsCompra()) {
            Movimiento movimiento = new Movimiento();
            movimiento.setCantidad(itemCompra.getCantidad()); // Positive quantity for stock increase
            movimiento.setProducto(itemCompra.getMateriaPrima());
            movimiento.setTipo(Movimiento.CausaMovimiento.COMPRA);
            movimiento.setObservaciones("Compra ID: " + savedCompra.getCompraId());
            movimientoRepo.save(movimiento);
        }

        return savedCompra;
    }

    private static int getNuevoCosto(ItemCompra itemCompra, Double currentStockOpt, MateriaPrima materiaPrima) {
        double currentStock = (currentStockOpt != null) ? currentStockOpt : 0;

        // Retrieve current costo
        double currentCosto = materiaPrima.getCosto();

        // Incoming units and precioCompra from ItemCompra
        double incomingUnits = itemCompra.getCantidad();
        double incomingPrecio = itemCompra.getPrecioCompra();

        // Calculate nuevo_costo
        if (currentStock + incomingUnits == 0) {
            throw new RuntimeException("Total stock cannot be zero after the compra for MateriaPrima ID: " + materiaPrima.getProductoId());
        }

        double nuevoCosto = ((currentCosto * currentStock) + (incomingPrecio * incomingUnits)) / (currentStock + incomingUnits);
        return  (int) Math.ceil(nuevoCosto);
    }

    private void updateCostoCascade(Producto producto, Set<Integer> updatedProductIds) {
        // If we've already updated this product, return to prevent infinite recursion
        if (updatedProductIds.contains(producto.getProductoId())) {
            return;
        }
        updatedProductIds.add(producto.getProductoId());

        // Recalculate cost of the product if it's a SemiTerminado or Terminado
        if (producto instanceof SemiTerminado) {
            SemiTerminado semiTerminado = (SemiTerminado) producto;

            // Recalculate cost
            double newCosto = 0;
            for (Insumo insumo : semiTerminado.getInsumos()) {
                Producto insumoProducto = insumo.getProducto();
                double insumoCosto = insumoProducto.getCosto();
                double cantidadRequerida = insumo.getCantidadRequerida();
                newCosto += insumoCosto * cantidadRequerida;
            }
            semiTerminado.setCosto((int) newCosto);

            // Save updated SemiTerminado
            semiTerminadoRepo.save(semiTerminado);

        } else if (producto instanceof Terminado) {
            Terminado terminado = (Terminado) producto;

            // Recalculate cost
            double newCosto = 0;
            for (Insumo insumo : terminado.getInsumos()) {
                Producto insumoProducto = insumo.getProducto();
                double insumoCosto = insumoProducto.getCosto();
                double cantidadRequerida = insumo.getCantidadRequerida();
                newCosto += insumoCosto * cantidadRequerida;
            }
            terminado.setCosto((int) newCosto);

            // Save updated Terminado
            terminadoRepo.save(terminado);
        }

        // Now find any SemiTerminados that use this product as an Insumo
        List<SemiTerminado> semiTerminados = semiTerminadoRepo.findByInsumos_Producto(producto);
        for (SemiTerminado st : semiTerminados) {
            updateCostoCascade(st, updatedProductIds);
        }

        // And find any Terminados that use this product as an Insumo
        List<Terminado> terminados = terminadoRepo.findByInsumos_Producto(producto);
        for (Terminado t : terminados) {
            updateCostoCascade(t, updatedProductIds);
        }
    }

    public Page<Compra> getComprasByProveedorAndDate(int proveedorId, String date1, String date2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDate = LocalDate.parse(date1).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(date2).atTime(LocalTime.MAX);
        return compraRepo.findByProveedorIdAndFechaCompraBetween(proveedorId, startDate, endDate, pageable);
    }

    public List<ItemCompra> getItemsByCompraId(int compraId) {
        Compra compra = compraRepo.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra not found with id: " + compraId));
        return compra.getItemsCompra();
    }


    /**
     *
     * Ordenes de Compra
     *
     */



    public OrdenCompra saveOrdenCompra(OrdenCompra ordenCompra) {
        // Verify the Proveedor exists
        Optional<Proveedor> optProveedor = proveedorRepo.findById(ordenCompra.getProveedor().getId());
        if (!optProveedor.isPresent()) {
            throw new RuntimeException("Proveedor not found with ID: " + ordenCompra.getProveedor().getId());
        }
        ordenCompra.setProveedor(optProveedor.get());

        // For each ItemOrdenCompra, set the back‑reference and initialize check fields to 0
        for (ItemOrdenCompra item : ordenCompra.getItemsOrdenCompra()) {
            item.setOrdenCompra(ordenCompra);
            if(item.getCantidadCorrecta() == 0) item.setCantidadCorrecta(0);
            if(item.getPrecioCorrecto() == 0) item.setPrecioCorrecto(0);
        }

        // Save and return the OrdenCompra
        return ordenCompraRepo.save(ordenCompra);
    }

    public Page<OrdenCompra> getOrdenesCompraByDateAndEstado(String date1, String date2, String estados, int page, int size) {
        // Parse the date strings
        LocalDateTime startDate = LocalDate.parse(date1).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(date2).atTime(LocalTime.MAX);

        // Parse the estados string into a list of integers
        List<Integer> estadoList = Arrays.stream(estados.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        return ordenCompraRepo.findByFechaEmisionBetweenAndEstadoIn(startDate, endDate, estadoList, pageable);
    }

    public OrdenCompra cancelOrdenCompra(int ordenCompraId) {
        OrdenCompra orden = ordenCompraRepo.findById(ordenCompraId)
                .orElseThrow(() -> new RuntimeException("OrdenCompra not found with id: " + ordenCompraId));
        orden.setEstado(-1);
        return ordenCompraRepo.save(orden);
    }

    public OrdenCompra updateEstadoOrdenCompra(int ordenCompraId, int newEstado) {
        OrdenCompra orden = ordenCompraRepo.findById(ordenCompraId)
                .orElseThrow(() -> new RuntimeException("OrdenCompra not found with id: " + ordenCompraId));
        orden.setEstado(newEstado);
        return ordenCompraRepo.save(orden);
    }

}

