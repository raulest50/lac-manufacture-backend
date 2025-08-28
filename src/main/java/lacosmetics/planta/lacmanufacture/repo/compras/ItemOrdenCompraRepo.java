package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.ItemOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrdenCompraRepo extends JpaRepository<ItemOrdenCompra, Integer> {
    boolean existsByMaterial_ProductoId(String productoId);
}

