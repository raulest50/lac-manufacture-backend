package lacosmetics.planta.lacmanufacture.service.productos.procesos;

import jakarta.validation.Valid;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoRecurso;
import lacosmetics.planta.lacmanufacture.repo.producto.procesos.ProcesoRecursoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProcesoRecursoService {

    private final ProcesoRecursoRepo procesoRecursoRepo;

    @Transactional
    public ProcesoRecurso saveProcesoRecurso(@Valid ProcesoRecurso procesoRecurso) {
        log.info("Guardando relación proceso-recurso");
        return procesoRecursoRepo.save(procesoRecurso);
    }

    @Transactional(readOnly = true)
    public List<ProcesoRecurso> getAllProcesoRecursos() {
        log.info("Obteniendo todas las relaciones proceso-recurso");
        return procesoRecursoRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ProcesoRecurso> getProcesoRecursoById(Long id) {
        log.info("Buscando relación proceso-recurso con ID: {}", id);
        return procesoRecursoRepo.findById(id);
    }

    @Transactional
    public void deleteProcesoRecurso(Long id) {
        log.info("Eliminando relación proceso-recurso con ID: {}", id);
        procesoRecursoRepo.deleteById(id);
    }
}