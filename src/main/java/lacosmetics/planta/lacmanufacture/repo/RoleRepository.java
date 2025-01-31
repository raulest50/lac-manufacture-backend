package lacosmetics.planta.lacmanufacture.repo;
import lacosmetics.planta.lacmanufacture.model.users.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // e.g. findByName if you want to create roles at runtime
    Role findByName(String name);
}



