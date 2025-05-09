package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProveedorRepo extends JpaRepository<Proveedor, String> {

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    Page<Proveedor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query("SELECT p FROM Proveedor p WHERE p.id = :id")
    Page<Proveedor> findById(@Param("id") int id, Pageable pageable);

    /**
     * Find providers whose ID starts with the given prefix
     */
    @Query("SELECT p FROM Proveedor p WHERE p.id LIKE :idPrefix%")
    Page<Proveedor> findByIdStartingWith(@Param("idPrefix") String idPrefix, Pageable pageable);

    /**
     * Search providers by name (optional) and categories (optional).
     * Categories are stored as PostgreSQL integer arrays, so this uses native SQL with the `&&` operator.
     */
    @Query(value = """
        SELECT * FROM proveedores p
        WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:categorias IS NULL OR p.categorias && CAST(:categorias AS int[]))
        """, nativeQuery = true)
    Page<Proveedor> searchByNombreAndCategorias(
            @Param("nombre") String nombre,
            @Param("categorias") int[] categorias,
            Pageable pageable
    );
}
