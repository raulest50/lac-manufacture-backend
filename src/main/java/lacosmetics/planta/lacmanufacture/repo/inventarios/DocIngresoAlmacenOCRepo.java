package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.real.DocIngresoAlmacenOC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocIngresoAlmacenOCRepo extends JpaRepository<DocIngresoAlmacenOC, Integer> {

}
