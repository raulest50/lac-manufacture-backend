package lacosmetics.planta.lacmanufacture.repo.ventas;

import lacosmetics.planta.lacmanufacture.model.ventas.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepo extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email);

    Page<Cliente> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email, Pageable pageable);

    /**
     * Find clients by ID
     */
    @Query("SELECT c FROM Cliente c WHERE c.clienteId = :id")
    Page<Cliente> findById(@Param("id") int id, Pageable pageable);

    /**
     * Search clients by name or email (optional)
     */
    @Query(value = """
        SELECT * FROM clientes c
        WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        OR (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
        """, nativeQuery = true)
    Page<Cliente> searchByNombreOrEmail(
            @Param("nombre") String nombre,
            @Param("email") String email,
            Pageable pageable
    );
}
