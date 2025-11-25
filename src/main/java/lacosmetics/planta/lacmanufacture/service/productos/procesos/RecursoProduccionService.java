package lacosmetics.planta.lacmanufacture.service.productos.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.dto.procdef.ReProdModDto;
import lacosmetics.planta.lacmanufacture.model.producto.dto.search.DTO_SearchRecursoProduccion;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.RecursoProduccion;
import lacosmetics.planta.lacmanufacture.repo.producto.procesos.RecursoProduccionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RecursoProduccionService {

    private final RecursoProduccionRepo recursoProduccionRepo;

    /**
     * Guarda un nuevo recurso de producción en la base de datos.
     * 
     * @param recursoProduccion El recurso de producción a guardar
     * @return El recurso de producción guardado con su ID asignado
     */
    @Transactional
    public RecursoProduccion saveRecursoProduccion(RecursoProduccion recursoProduccion) {
        log.info("Guardando recurso de producción: {}", recursoProduccion.getNombre());
        return recursoProduccionRepo.save(recursoProduccion);
    }

    /**
     * Busca recursos de producción según los criterios especificados.
     * 
     * @param searchDTO DTO con los criterios de búsqueda
     * @return Página de recursos de producción que cumplen con los criterios
     */
    @Transactional(readOnly = true)
    public Page<RecursoProduccion> searchRecursosProduccion(DTO_SearchRecursoProduccion searchDTO) {
        log.info("Buscando recursos de producción con criterios: {}", searchDTO);

        // Configurar paginación
        int page = searchDTO.getPage() != null ? searchDTO.getPage() : 0;
        int size = searchDTO.getSize() != null ? searchDTO.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        // Realizar búsqueda según el tipo
        if (searchDTO.getTipoBusqueda() == DTO_SearchRecursoProduccion.TipoBusqueda.POR_ID && searchDTO.getValorBusqueda() != null) {
            try {
                Long id = Long.parseLong(searchDTO.getValorBusqueda());
                Optional<RecursoProduccion> recurso = recursoProduccionRepo.findById(id);
                if (recurso.isPresent()) {
                    return new org.springframework.data.domain.PageImpl<>(
                            java.util.Collections.singletonList(recurso.get()), 
                            pageable, 
                            1
                    );
                } else {
                    return new org.springframework.data.domain.PageImpl<>(
                            java.util.Collections.emptyList(), 
                            pageable, 
                            0
                    );
                }
            } catch (NumberFormatException e) {
                log.warn("Valor de ID no válido: {}", searchDTO.getValorBusqueda());
                return new org.springframework.data.domain.PageImpl<>(
                        java.util.Collections.emptyList(), 
                        pageable, 
                        0
                );
            }
        } else if (searchDTO.getTipoBusqueda() == DTO_SearchRecursoProduccion.TipoBusqueda.POR_NOMBRE) {
            // Búsqueda por nombre (coincidencia parcial)
            String nombreBusqueda = searchDTO.getValorBusqueda() != null && !searchDTO.getValorBusqueda().trim().isEmpty() ? 
                    searchDTO.getValorBusqueda().toLowerCase() : "";

            Specification<RecursoProduccion> spec = (root, query, cb) -> 
                cb.like(cb.lower(root.get("nombre")), "%" + nombreBusqueda + "%");

            return recursoProduccionRepo.findAll(spec, pageable);
        } else {
            // Si no se especifica tipo de búsqueda o valor, devolver todos paginados
            return recursoProduccionRepo.findAll(pageable);
        }
    }

    /**
     * Actualiza un recurso de producción existente con validación.
     * 
     * @param modDto DTO con el recurso original y el actualizado
     * @return El recurso de producción actualizado
     * @throws IllegalArgumentException si la validación falla
     */
    @Transactional
    public RecursoProduccion updateRecursoProduccion(ReProdModDto modDto) {
        log.info("Actualizando recurso de producción");

        // Validar que el recurso original existe y coincide con el almacenado
        RecursoProduccion oldRecurso = modDto.getOldRecursoProduccion();
        RecursoProduccion newRecurso = modDto.getNewRecursoProduccion();

        if (oldRecurso == null || newRecurso == null) {
            throw new IllegalArgumentException("Los recursos de producción no pueden ser nulos");
        }

        if (oldRecurso.getId() == null) {
            throw new IllegalArgumentException("El ID del recurso original no puede ser nulo");
        }

        // Verificar que el recurso existe en la base de datos
        Optional<RecursoProduccion> existingRecursoOpt = recursoProduccionRepo.findById(oldRecurso.getId());
        if (!existingRecursoOpt.isPresent()) {
            throw new IllegalArgumentException("El recurso de producción con ID " + oldRecurso.getId() + " no existe");
        }

        RecursoProduccion existingRecurso = existingRecursoOpt.get();

        // Verificar que el recurso original coincide con el almacenado
        if (!Objects.equals(existingRecurso.getNombre(), oldRecurso.getNombre())) {
            throw new IllegalArgumentException("El nombre del recurso original no coincide con el almacenado");
        }

        // Verificar que el ID no cambia
        if (!Objects.equals(oldRecurso.getId(), newRecurso.getId())) {
            throw new IllegalArgumentException("No se puede cambiar el ID del recurso de producción");
        }

        // Actualizar el recurso
        log.info("Actualizando recurso de producción con ID: {}", newRecurso.getId());
        return recursoProduccionRepo.save(newRecurso);
    }
}
