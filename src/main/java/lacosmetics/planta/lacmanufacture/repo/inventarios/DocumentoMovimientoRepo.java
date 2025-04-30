package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.DocMovs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoMovimientoRepo extends JpaRepository<DocMovs, Integer> {
}
