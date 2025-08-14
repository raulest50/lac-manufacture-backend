package lacosmetics.planta.lacmanufacture.service.productos.procesos;

import lacosmetics.planta.lacmanufacture.dto.ProcesoProduccionDTO;
import lacosmetics.planta.lacmanufacture.dto.RecursoProduccionDTO;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccion;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoRecurso;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion;
import lacosmetics.planta.lacmanufacture.repo.producto.procesos.ProcesoProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.procesos.RecursoProduccionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcesoProduccionService {

    private final ProcesoProduccionRepo procesoProduccionRepo;
    private final RecursoProduccionRepo recursoProduccionRepo;
    private final ProcesoRecursoService procesoRecursoService;

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

    /**
     * Crea un proceso de producción a partir de un DTO, incluyendo la asignación de recursos con cantidades
     * 
     * @param dto El DTO con la información del proceso y sus recursos
     * @return El proceso de producción creado
     * @throws RuntimeException si no se encuentra algún recurso
     */
    @Transactional
    public ProcesoProduccion createProcesoProduccionFromDTO(ProcesoProduccionDTO dto) {
        log.info("Creando proceso de producción desde DTO: {}", dto.getNombre());

        // Crear y guardar el proceso primero para obtener su ID
        ProcesoProduccion proceso = new ProcesoProduccion();
        proceso.setNombre(dto.getNombre());
        proceso.setSetUpTime(dto.getSetUpTime());
        proceso.setProcessTime(dto.getProcessTime());
        proceso.setNivelAcceso(dto.getNivelAcceso() != null ? dto.getNivelAcceso() : 1);

        // Guardar el proceso para obtener su ID
        proceso = procesoProduccionRepo.save(proceso);

        // Crear las relaciones ProcesoRecurso con las cantidades
        if (dto.getRecursosRequeridos() != null && !dto.getRecursosRequeridos().isEmpty()) {
            List<ProcesoRecurso> recursosRequeridos = new ArrayList<>();

            for (RecursoProduccionDTO recursoDTO : dto.getRecursosRequeridos()) {
                if (recursoDTO.getId() == null) {
                    throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
                }

                // Buscar el recurso por ID
                RecursoProduccion recurso = recursoProduccionRepo.findById(recursoDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado con ID: " + recursoDTO.getId()));

                // Crear la relación proceso-recurso
                ProcesoRecurso procesoRecurso = new ProcesoRecurso();
                procesoRecurso.setProceso(proceso);
                procesoRecurso.setRecurso(recurso);

                // Asignar la cantidad (nunca menor a 1)
                Integer cantidad = recursoDTO.getCantidad();
                if (cantidad == null || cantidad < 1) {
                    log.warn("Cantidad inválida para el recurso ID {}: {}. Se establecerá a 1.", 
                            recursoDTO.getId(), cantidad);
                    cantidad = 1;
                }
                procesoRecurso.setCantidad(cantidad);

                // Guardar la relación
                procesoRecurso = procesoRecursoService.saveProcesoRecurso(procesoRecurso);
                recursosRequeridos.add(procesoRecurso);
            }

            // Asignar las relaciones al proceso
            proceso.setRecursosRequeridos(recursosRequeridos);
        } else {
            log.warn("No se especificaron recursos para el proceso: {}", dto.getNombre());
        }

        return proceso;
    }

    /**
     * Actualiza un proceso de producción existente a partir de un DTO, incluyendo la actualización de recursos con cantidades
     * 
     * @param id El ID del proceso a actualizar
     * @param dto El DTO con la información actualizada del proceso y sus recursos
     * @return El proceso de producción actualizado
     * @throws RuntimeException si no se encuentra el proceso o algún recurso
     */
    @Transactional
    public ProcesoProduccion updateProcesoProduccionFromDTO(Integer id, ProcesoProduccionDTO dto) {
        log.info("Actualizando proceso de producción con ID {} desde DTO", id);

        if (id == null) {
            throw new IllegalArgumentException("El ID del proceso no puede ser nulo");
        }

        // Verificar que el proceso existe
        ProcesoProduccion proceso = procesoProduccionRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proceso de producción no encontrado con ID: " + id));

        // Actualizar los datos básicos del proceso
        proceso.setNombre(dto.getNombre());
        proceso.setSetUpTime(dto.getSetUpTime());
        proceso.setProcessTime(dto.getProcessTime());
        proceso.setNivelAcceso(dto.getNivelAcceso() != null ? dto.getNivelAcceso() : 1);

        // Guardar los cambios básicos
        proceso = procesoProduccionRepo.save(proceso);

        // Eliminar las relaciones existentes para recrearlas
        if (proceso.getRecursosRequeridos() != null) {
            log.info("Eliminando {} relaciones de recursos existentes para el proceso ID: {}", 
                    proceso.getRecursosRequeridos().size(), id);
            proceso.getRecursosRequeridos().clear();
        }

        // Crear las nuevas relaciones ProcesoRecurso con las cantidades
        if (dto.getRecursosRequeridos() != null && !dto.getRecursosRequeridos().isEmpty()) {
            List<ProcesoRecurso> recursosRequeridos = new ArrayList<>();

            for (RecursoProduccionDTO recursoDTO : dto.getRecursosRequeridos()) {
                if (recursoDTO.getId() == null) {
                    throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
                }

                // Buscar el recurso por ID
                RecursoProduccion recurso = recursoProduccionRepo.findById(recursoDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado con ID: " + recursoDTO.getId()));

                // Crear la relación proceso-recurso
                ProcesoRecurso procesoRecurso = new ProcesoRecurso();
                procesoRecurso.setProceso(proceso);
                procesoRecurso.setRecurso(recurso);

                // Asignar la cantidad (nunca menor a 1)
                Integer cantidad = recursoDTO.getCantidad();
                if (cantidad == null || cantidad < 1) {
                    log.warn("Cantidad inválida para el recurso ID {}: {}. Se establecerá a 1.", 
                            recursoDTO.getId(), cantidad);
                    cantidad = 1;
                }
                procesoRecurso.setCantidad(cantidad);

                // Guardar la relación
                procesoRecurso = procesoRecursoService.saveProcesoRecurso(procesoRecurso);
                recursosRequeridos.add(procesoRecurso);
            }

            // Asignar las relaciones al proceso
            proceso.setRecursosRequeridos(recursosRequeridos);
            log.info("Se asignaron {} recursos al proceso ID: {}", recursosRequeridos.size(), id);
        } else {
            log.warn("No se especificaron recursos para el proceso ID: {}", id);
        }

        return proceso;
    }
}
