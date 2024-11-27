package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProveedorRepo extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    Page<Proveedor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query("SELECT p FROM Proveedor p WHERE p.id = :id")
    Page<Proveedor> findById(@Param("id") int id, Pageable pageable);

}
