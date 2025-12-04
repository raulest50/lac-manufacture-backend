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

    /**
     * Busca los insumos asociados a una versión de manufactura.
     *
     * @param manufacturingVersionId ID de la versión de manufactura
     * @return Lista de insumos relacionados a la versión
     */
    java.util.List<Insumo> findByManufacturingVersion_Id(Long manufacturingVersionId);

    /**
     * Busca los insumos asociados a versiones de manufactura de un producto.
     *
     * @param productoId ID del producto propietario de las versiones
     * @return Lista de insumos relacionados a las versiones del producto
     */
    java.util.List<Insumo> findByManufacturingVersion_Producto_ProductoId(String productoId);
}

