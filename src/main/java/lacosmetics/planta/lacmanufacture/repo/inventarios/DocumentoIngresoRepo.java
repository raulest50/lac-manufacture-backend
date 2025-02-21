package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.DocumentoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoIngresoRepo extends JpaRepository<DocumentoMovimiento, Integer> {
}
