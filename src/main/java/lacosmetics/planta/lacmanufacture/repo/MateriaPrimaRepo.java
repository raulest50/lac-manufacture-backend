package lacosmetics.planta.lacmanufacture.repo;

import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaPrimaRepo extends JpaRepository<MateriaPrima, Integer>, JpaSpecificationExecutor<MateriaPrima> {

    // Método para encontrar las materias primas que cumplen con la condición
    Page<MateriaPrima> findByContenidoPorUnidadIsOrTipoUnidadesIsNull(double contenidoPorUnidad, Pageable pageable);

}
