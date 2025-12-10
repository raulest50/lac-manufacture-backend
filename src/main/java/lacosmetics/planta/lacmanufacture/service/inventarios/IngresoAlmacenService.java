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

import java.util.Collections;

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
}
