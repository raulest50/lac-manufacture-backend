package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenCompraRepo extends JpaRepository<OrdenCompraMateriales, Integer> {

    Page<OrdenCompraMateriales> findByFechaEmisionBetweenAndEstadoIn(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<Integer> estados,
            Pageable pageable
    );

    Optional<OrdenCompraMateriales> findByOrdenCompraIdAndEstado(Integer ordenCompraId, int estado);

}
