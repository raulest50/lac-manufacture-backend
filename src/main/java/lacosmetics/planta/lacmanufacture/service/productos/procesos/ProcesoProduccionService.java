package lacosmetics.planta.lacmanufacture.service.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccion;
import lacosmetics.planta.lacmanufacture.repo.producto.procesos.ProcesoProduccionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcesoProduccionService {

    private final ProcesoProduccionRepo procesoProduccionRepo;

    @Transactional
    public ProcesoProduccion saveProcesoProduccion(ProcesoProduccion procesoProduccion) {
        log.info("Guardando proceso de producción: {}", procesoProduccion.getNombre());
        return procesoProduccionRepo.save(procesoProduccion);
    }

    @Transactional(readOnly = true)
    public Page<ProcesoProduccion> getProcesosProduccionPaginados(Pageable pageable) {
        log.info("Obteniendo procesos de producción paginados");
        return procesoProduccionRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<ProcesoProduccion> getProcesoProduccionById(Integer id) {
        log.info("Buscando proceso de producción con ID: {}", id);
        return procesoProduccionRepo.findById(id);
    }
}