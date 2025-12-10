package lacosmetics.planta.lacmanufacture.service.inventarios;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.OCMReceptionInfoDTO;
import lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion.SearchOCMFilterDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngresoAlmacenService {

    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;
    private final OrdenCompraRepo ordenCompraRepo;

    public Page<OCMReceptionInfoDTO> consultaOCMPendientes(SearchOCMFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaTransaccion").descending());

        Page<TransaccionAlmacen> transacciones = transaccionAlmacenHeaderRepo.findByFilters(
                TransaccionAlmacen.EstadoContable.PENDIENTE,
                TransaccionAlmacen.TipoEntidadCausante.OCM,
                null,
                null,
                pageable
        );

        return transacciones.map(transaccion -> {
            OCMReceptionInfoDTO dto = new OCMReceptionInfoDTO();
            OrdenCompraMateriales ordenCompra = ordenCompraRepo.findById(transaccion.getIdEntidadCausante())
                    .orElse(null);

            dto.ordenCompraMateriales = ordenCompra;
            dto.transaccionesAlmacen = Collections.singletonList(transaccion);
            return dto;
        });
    }

    public Page<OCMReceptionInfoDTO> consultaOCMConRecepciones(SearchOCMFilterDTO filter, int page, int size) {
        LocalDateTime fechaInicio = filter.getFechaInicio() != null ? filter.getFechaInicio() : LocalDateTime.MIN;
        LocalDateTime fechaFin = filter.getFechaFin() != null ? filter.getFechaFin() : LocalDateTime.now();
        String proveedorId = filter.getProveedorId() != null ? String.valueOf(filter.getProveedorId()) : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEmision").descending());
        List<Integer> estados = Collections.singletonList(2);

        Page<OrdenCompraMateriales> ordenesCompra = ordenCompraRepo
                .findByFechaEmisionBetweenAndEstadoInAndProveedor(fechaInicio, fechaFin, estados, proveedorId, pageable);

        return ordenesCompra.map(ordenCompra -> {
            List<TransaccionAlmacen> transacciones = transaccionAlmacenHeaderRepo
                    .findByTipoEntidadCausanteAndIdEntidadCausante(
                            TransaccionAlmacen.TipoEntidadCausante.OCM,
                            ordenCompra.getOrdenCompraId()
                    );

            OCMReceptionInfoDTO dto = new OCMReceptionInfoDTO();
            dto.ordenCompraMateriales = ordenCompra;
            dto.transaccionesAlmacen = transacciones;
            return dto;
        });
    }
}
