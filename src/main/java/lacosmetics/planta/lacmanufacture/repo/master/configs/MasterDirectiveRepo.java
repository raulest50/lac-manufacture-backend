package lacosmetics.planta.lacmanufacture.repo.master.configs;

import lacosmetics.planta.lacmanufacture.model.master.configs.MasterDirective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterDirectiveRepo extends JpaRepository<MasterDirective, Long> {
    // Consultas personalizadas si son necesarias
    MasterDirective findByNombre(String nombre);
}