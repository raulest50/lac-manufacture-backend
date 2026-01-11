package exotic.app.planta.repo.compras;

import exotic.app.planta.model.compras.ItemFacturaCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemFacturaCompraRepo extends JpaRepository<ItemFacturaCompra, Integer> {
    // Additional query methods can be added here if needed
}
