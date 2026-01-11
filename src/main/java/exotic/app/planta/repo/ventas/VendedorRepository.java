package exotic.app.planta.repo.ventas;

import exotic.app.planta.model.ventas.Vendedor;
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
