package lacosmetics.planta.lacmanufacture.repo.contabilidad;

import lacosmetics.planta.lacmanufacture.model.contabilidad.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepo extends JpaRepository<Cuenta, String> {
    // Aquí puedes declarar consultas personalizadas si las necesitas, por ejemplo:
    // List<Cuenta> findByTipo(TipoCuenta tipo);
}
