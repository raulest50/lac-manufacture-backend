package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.TerminadoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProduccionService {


    private final OrdenProduccionRepo ordenProduccionRepo;
    private final TerminadoRepo terminadoRepo;

    public Page<OrdenProduccion> getWorkloadByZona(int zonaId, int page, int size) {
        List<OrdenProduccion> listaWorkload = ordenProduccionRepo.
                findBySeccionResponsableAndEstadoOrden(zonaId, 0);

        Sort sort = Sort.by("fechaInicio").ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return new PageImpl<>(listaWorkload, pageRequest, listaWorkload.size());
    }

    @Transactional(rollbackOn = Exception.class)
    public OrdenProduccion saveOrdenProduccion(OrdenProduccionDTA ordenProduccionDTA)
    {
        Terminado terminado = terminadoRepo.findById(ordenProduccionDTA.terminadoId).get();
        OrdenProduccion ordenProduccion = new OrdenProduccion(terminado, ordenProduccionDTA.seccionResponsable, ordenProduccionDTA.observaciones);
        return ordenProduccionRepo.save(ordenProduccion);
    }

    public Page<OrdenProduccion> getAllByEstado(int page, int size, int estado) {
        List<OrdenProduccion> lista = ordenProduccionRepo.findByEstadoOrden(estado);
        Sort sort = Sort.by("fechaInicio").ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return new PageImpl<>(lista, pageRequest, lista.size());
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrdenProduccionDTA{
        private int terminadoId;
        int seccionResponsable;
        String observaciones;
    }
}
