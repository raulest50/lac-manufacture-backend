package lacosmetics.planta.lacmanufacture.repo.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad AsientoContable.
 * Proporciona operaciones CRUD básicas para asientos contables.
 */
@Repository
public interface AsientoContableRepo extends JpaRepository<AsientoContable, Long> {
    // Aquí se pueden agregar consultas personalizadas según sea necesario
}