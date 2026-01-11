package exotic.app.planta.repo.activos.fijos;

import exotic.app.planta.model.activos.fijos.compras.ItemOrdenCompraActivo;
import exotic.app.planta.model.activos.fijos.compras.OrdenCompraActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar las operaciones de persistencia de los ítems de órdenes de compra de activos fijos.
 */
@Repository
public interface ItemOrdenCompraActivoRepo extends JpaRepository<ItemOrdenCompraActivo, Integer> {

    /**
     * Busca todos los ítems asociados a una orden de compra específica.
     * 
     * @param ordenCompraActivo la orden de compra
     * @return lista de ítems de la orden
     */
    List<ItemOrdenCompraActivo> findByOrdenCompraActivo(OrdenCompraActivo ordenCompraActivo);

    /**
     * Busca todos los ítems asociados a una orden de compra por su ID.
     * 
     * @param ordenCompraActivoId ID de la orden de compra
     * @return lista de ítems de la orden
     */
    List<ItemOrdenCompraActivo> findByOrdenCompraActivo_OrdenCompraActivoId(int ordenCompraActivoId);

    /**
     * Elimina todos los ítems asociados a una orden de compra por su ID.
     * 
     * @param ordenCompraActivoId ID de la orden de compra
     */
    void deleteByOrdenCompraActivo_OrdenCompraActivoId(Integer ordenCompraActivoId);
}
