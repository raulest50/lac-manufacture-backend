package lacosmetics.planta.lacmanufacture.repo.producto;

import lacosmetics.planta.lacmanufacture.model.producto.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepo extends JpaRepository<Material, String>, JpaSpecificationExecutor<Material> {


}
