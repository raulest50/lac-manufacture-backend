package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Movimiento;
import lacosmetics.planta.lacmanufacture.model.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.OrdenSeguimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.TerminadoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final MovimientoRepo movmientoRepo;

    @Autowired
    private final OrdenSeguimientoRepo ordenSeguimientoRepo;
    @Autowired
    private MovimientoRepo movimientoRepo;

    public Page<OrdenSeguimiento> getWorkloadByZona(int zonaId, int page, int size) {
        List<OrdenSeguimiento> listaWorkload = ordenSeguimientoRepo.findBySeccionResponsable(zonaId);
        Sort sort = Sort.by("fechaInicio").ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return new PageImpl<>(listaWorkload, pageRequest, listaWorkload.size());
    }


    public Page<OrdenProduccion> getOrdenesProdByZona(int zonaId, int page, int size) {
        List<OrdenProduccion> listaWorkload = ordenProduccionRepo.findBySeccionResponsableAndEstadoOrden(zonaId, 0);
        Sort sort = Sort.by("fechaInicio").ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return new PageImpl<>(listaWorkload, pageRequest, listaWorkload.size());
    }



    @Transactional(rollbackOn = Exception.class)
    public OrdenProduccion saveOrdenProduccion(OrdenProduccionDTA ordenProduccionDTA)
    {
        Terminado terminado = terminadoRepo.findById(ordenProduccionDTA.terminadoId).get();
        OrdenProduccion ordenProduccion = new OrdenProduccion(terminado, ordenProduccionDTA.observaciones);
        return ordenProduccionRepo.save(ordenProduccion);
    }

    public Page<OrdenProduccion> getAllByEstado(int page, int size, int estado) {
        List<OrdenProduccion> lista = ordenProduccionRepo.findByEstadoOrden(estado);
        Sort sort = Sort.by("fechaInicio").ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return new PageImpl<>(lista, pageRequest, lista.size());
    }


    /**
     * Una orden de seguimiento completada implica actualizar los movimientos correspondientes de los insumos que
     * entran y del producto que sale, semiterminado o terminado segun sea el caso.
     *
     * @param seguimientoId
     * @param estado
     * @return
     */
    @Transactional
    public OrdenSeguimiento updateEstadoOrdenSeguimiento(int seguimientoId, int estado) {

        if(estado == 1){
            ordenSeguimientoRepo.updateEstadoById(seguimientoId, estado);
            OrdenSeguimiento ordenSeguimiento = ordenSeguimientoRepo.findById(seguimientoId).orElse(null);
            Movimiento movimientoIn = new Movimiento(ordenSeguimiento.getInsumo());
            movimientoRepo.save(movimientoIn);

        }

        return ordenSeguimientoRepo.findById(seguimientoId).orElse(null);
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrdenProduccionDTA{
        private int terminadoId;
        //int seccionResponsable; // se especifica a la hora de codificar terminado y semiterminado
        String observaciones;
    }
}
