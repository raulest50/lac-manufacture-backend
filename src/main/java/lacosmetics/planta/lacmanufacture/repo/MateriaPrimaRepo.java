package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.MateriaPrima;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaPrimaRepo extends JpaRepository<MateriaPrima, Integer>, JpaSpecificationExecutor<MateriaPrima> {


}
