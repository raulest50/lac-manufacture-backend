package lacosmetics.planta.lacmanufacture.service;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.*;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.*;
import lacosmetics.planta.lacmanufacture.repo.producto.MateriaPrimaRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


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
            movimiento.setCausa(Movimiento.CausaMovimiento.COMPRA);
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


    /**
     * Recursively updates the 'costo' of dependent products when a product's cost changes.
     *
     * @param producto          The product whose dependents need to be updated.
     * @param updatedProductIds A set to track updated products and prevent infinite recursion.
     */
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
}

