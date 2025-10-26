package lacosmetics.planta.lacmanufacture.repo.producto.procesos;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.AreaProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaProduccionRepo extends JpaRepository<AreaProduccion, Integer> {
    Optional<AreaProduccion> findByNombre(String nombre);
}