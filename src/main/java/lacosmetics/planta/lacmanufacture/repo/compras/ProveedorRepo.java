package lacosmetics.planta.lacmanufacture.repo.compras;

import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProveedorRepo extends JpaRepository<Proveedor, UUID> {

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    Page<Proveedor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Find a provider by its business identifier
     */
    Optional<Proveedor> findById(String id);

    /**
     * Find providers whose business identifier equals the given id
     */
    @Query("SELECT p FROM Proveedor p WHERE p.id = :id")
    Page<Proveedor> findByBusinessId(@Param("id") String id, Pageable pageable);

    /**
     * Find providers whose business identifier starts with the given prefix
     */
    @Query("SELECT p FROM Proveedor p WHERE p.id LIKE CONCAT(:idPrefix, '%')")
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
