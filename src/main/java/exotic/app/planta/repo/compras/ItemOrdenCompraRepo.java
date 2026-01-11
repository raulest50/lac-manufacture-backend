package exotic.app.planta.repo.compras;

import exotic.app.planta.model.compras.ItemOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrdenCompraRepo extends JpaRepository<ItemOrdenCompra, Integer> {
    boolean existsByMaterial_ProductoId(String productoId);
}

