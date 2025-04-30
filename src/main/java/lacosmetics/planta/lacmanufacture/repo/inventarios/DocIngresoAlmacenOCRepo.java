package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.formatos.IngresoOCM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocIngresoAlmacenOCRepo extends JpaRepository<IngresoOCM, Integer> {

}
