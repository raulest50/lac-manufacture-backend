package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepo extends JpaRepository<Producto, Integer>{

    Optional<Producto> findById(int id);


    @Query("SELECT p FROM Producto p WHERE p.tipo_producto IN 1")
    Page<Producto> findAllByTipoProductoIn(Pageable pageable, String[] tipo_producto);

}
