package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.compras.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCompraRepo extends JpaRepository<ItemCompra, Integer> {
    // Additional query methods can be added here if needed
}
