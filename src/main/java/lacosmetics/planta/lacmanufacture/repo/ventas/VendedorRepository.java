package lacosmetics.planta.lacmanufacture.repo.ventas;

import lacosmetics.planta.lacmanufacture.model.ventas.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    boolean existsByCedula(long cedula);

    Page<Vendedor> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres,
                                                                                    String apellidos,
                                                                                    Pageable pageable);

    Page<Vendedor> findByCedula(long cedula, Pageable pageable);
}
