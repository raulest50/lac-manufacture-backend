package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.ItemFacturaCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemFacturaCompraRepo extends JpaRepository<ItemFacturaCompra, Integer> {
    // Additional query methods can be added here if needed
}
