package lacosmetics.planta.lacmanufacture.repo.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.LineaAsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad LineaAsientoContable.
 * Proporciona operaciones CRUD básicas para líneas de asientos contables.
 */
@Repository
public interface LineaAsientoContableRepo extends JpaRepository<LineaAsientoContable, Long> {
    // Aquí se pueden agregar consultas personalizadas según sea necesario
}