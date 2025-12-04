package lacosmetics.planta.lacmanufacture.repo.producto;

import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepo extends JpaRepository<Insumo, Integer> {

    /**
     * Busca todos los insumos que referencian a un producto de entrada específico.
     *
     * @param productoId ID del producto referenciado en la receta
     * @return Lista de insumos relacionados
     */
    java.util.List<Insumo> findByProducto_ProductoId(String productoId);
}

