package lacosmetics.planta.lacmanufacture.repo.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaContableRepo extends JpaRepository<CuentaContable, String> {
    // Aquí puedes declarar consultas personalizadas si las necesitas, por ejemplo:
    // List<CuentaContable> findByTipo(TipoCuenta tipo);
}
