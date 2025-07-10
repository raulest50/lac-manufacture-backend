package lacosmetics.planta.lacmanufacture.repo.ventas;

import lacosmetics.planta.lacmanufacture.model.ventas.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepo extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email);
}
