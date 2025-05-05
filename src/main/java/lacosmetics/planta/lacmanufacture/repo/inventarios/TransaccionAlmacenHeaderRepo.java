package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionAlmacenHeaderRepo extends JpaRepository<TransaccionAlmacen, Integer> {
}
