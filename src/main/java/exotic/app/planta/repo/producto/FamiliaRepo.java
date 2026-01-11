package exotic.app.planta.repo.producto;

import exotic.app.planta.model.producto.Familia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamiliaRepo extends JpaRepository<Familia, Integer> {
    
    /**
     * Checks if a family with the given name exists
     * @param familiaNombre the name to check
     * @return true if a family with the given name exists, false otherwise
     */
    boolean existsByFamiliaNombre(String familiaNombre);
    
    /**
     * Finds a family by its name
     * @param familiaNombre the name to search for
     * @return the family with the given name, or null if none exists
     */
    Familia findByFamiliaNombre(String familiaNombre);
}