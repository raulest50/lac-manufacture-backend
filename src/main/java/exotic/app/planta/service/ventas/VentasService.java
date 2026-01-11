package exotic.app.planta.service.ventas;

import exotic.app.planta.model.ventas.FacturaVenta;
import exotic.app.planta.model.ventas.OrdenVenta;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.ventas.*;
import exotic.app.planta.repo.ventas.FacturaVentaRepo;
import exotic.app.planta.repo.ventas.OrdenVentaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VentasService {

    private final OrdenVentaRepo ordenVentaRepo;
    private final FacturaVentaRepo facturaVentaRepo;

    /* Ordenes */
    public OrdenVenta saveOrdenVenta(OrdenVenta orden) {
        if (orden.getItemsOrdenVenta() != null) {
            orden.getItemsOrdenVenta().forEach(i -> i.setOrdenVenta(orden));
        }
        return ordenVentaRepo.save(orden);
    }

    public Optional<OrdenVenta> findOrdenVentaById(int id) {
        return ordenVentaRepo.findById(id);
    }

    public Page<OrdenVenta> searchOrdenes(Integer clienteId, String estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ordenVentaRepo.findByClienteAndEstado(clienteId, estado, pageable);
    }

    public OrdenVenta updateEstadoOrden(int id, String estado) {
        OrdenVenta orden = ordenVentaRepo.findById(id).orElseThrow(() -> new RuntimeException("OrdenVenta not found"));
        orden.setEstado(estado);
        return ordenVentaRepo.save(orden);
    }

    /* Facturas */
    public FacturaVenta saveFacturaVenta(FacturaVenta factura) {
        if (factura.getItemsFacturaVenta() != null) {
            factura.getItemsFacturaVenta().forEach(i -> i.setFacturaVenta(factura));
        }
        return facturaVentaRepo.save(factura);
    }

    public Optional<FacturaVenta> findFacturaVentaById(int id) {
        return facturaVentaRepo.findById(id);
    }

    public Page<FacturaVenta> searchFacturas(Integer clienteId, String estadoPago, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return facturaVentaRepo.findByClienteAndEstado(clienteId, estadoPago, pageable);
    }

    public FacturaVenta updateEstadoFactura(int id, String estado) {
        FacturaVenta factura = facturaVentaRepo.findById(id).orElseThrow(() -> new RuntimeException("FacturaVenta not found"));
        factura.setEstadoPago(estado);
        return facturaVentaRepo.save(factura);
    }
}
