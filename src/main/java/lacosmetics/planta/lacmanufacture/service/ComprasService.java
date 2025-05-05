package lacosmetics.planta.lacmanufacture.service;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.compras.*;
import lacosmetics.planta.lacmanufacture.model.dto.UpdateEstadoOrdenCompraRequest;
import lacosmetics.planta.lacmanufacture.repo.compras.FacturaCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
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

    private final FacturaCompraRepo facturaCompraRepo;
    private final TransaccionAlmacenRepo transaccionAlmacenRepo;
    private final ProveedorRepo proveedorRepo;
    private final MaterialRepo materialRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;

    private final OrdenCompraRepo ordenCompraRepo;

    /**
     *
     * Compras
     *
     */



    public Page<FacturaCompra> getComprasByProveedorAndDate(int proveedorId, String date1, String date2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDate = LocalDate.parse(date1).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(date2).atTime(LocalTime.MAX);
        return facturaCompraRepo.findByProveedorIdAndFechaCompraBetween(proveedorId, startDate, endDate, pageable);
    }

    public List<ItemFacturaCompra> getItemsByCompraId(int compraId) {
        FacturaCompra facturaCompra = facturaCompraRepo.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra not found with id: " + compraId));
        return facturaCompra.getItemsCompra();
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

    public OrdenCompra updateEstadoOrdenCompra(int ordenCompraId, UpdateEstadoOrdenCompraRequest ue) {
        OrdenCompra orden = ordenCompraRepo.findById(ordenCompraId).orElseThrow(() -> new RuntimeException("OrdenCompra not found with id: " + ordenCompraId));
        orden.setEstado(ue.getNewEstado());
        return ordenCompraRepo.save(orden);
    }

    public OrdenCompra getOrdenCompraByOrdenCompraIdAndEstado(Integer ordenCompraId, int estado) {
        return ordenCompraRepo.findByOrdenCompraIdAndEstado(ordenCompraId, estado)
                .orElseThrow(() -> new RuntimeException("OrdenCompra not found with OrdenCompraId: "
                        + ordenCompraId + " and estado = " + estado));
    }

}

