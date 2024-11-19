package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepo extends JpaRepository<Compra, Integer> {
    // Additional query methods can be added here if needed
}
