package lacosmetics.planta.lacmanufacture.repo.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de activos fijos.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre la entidad ActivoFijo.
 */
@Repository
public interface ActivoFijoRepo extends JpaRepository<ActivoFijo, String>, JpaSpecificationExecutor<ActivoFijo> {
    
    /**
     * Busca activos fijos por ubicación.
     * 
     * @param ubicacion La ubicación a buscar
     * @return Lista de activos fijos que coinciden con la ubicación especificada
     */
    java.util.List<ActivoFijo> findByUbicacion(String ubicacion);
    
    /**
     * Busca activos fijos por responsable.
     * 
     * @param responsableId El ID del responsable
     * @return Lista de activos fijos asignados al responsable especificado
     */
    java.util.List<ActivoFijo> findByResponsableId(long responsableId);
    
    /**
     * Busca activos fijos por tipo.
     * 
     * @param tipoActivo El tipo de activo a buscar
     * @return Lista de activos fijos del tipo especificado
     */
    java.util.List<ActivoFijo> findByTipoActivo(ActivoFijo.TipoActivo tipoActivo);
}