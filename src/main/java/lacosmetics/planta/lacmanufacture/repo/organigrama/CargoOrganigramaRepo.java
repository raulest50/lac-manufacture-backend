package lacosmetics.planta.lacmanufacture.repo.organigrama;

import lacosmetics.planta.lacmanufacture.model.organigrama.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoOrganigramaRepo extends JpaRepository<Cargo, String> {
    // Métodos básicos de CRUD proporcionados por JpaRepository
}
