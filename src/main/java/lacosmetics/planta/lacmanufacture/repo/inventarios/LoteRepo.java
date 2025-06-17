package lacosmetics.planta.lacmanufacture.repo.inventarios;

import lacosmetics.planta.lacmanufacture.model.inventarios.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Lote.
 * Proporciona operaciones CRUD básicas para la entidad Lote.
 */
public interface LoteRepo extends JpaRepository<Lote, Long> {
    
    /**
     * Busca un lote por su número de batch.
     * 
     * @param batchNumber El número de batch a buscar
     * @return El lote encontrado, o null si no existe
     */
    Lote findByBatchNumber(String batchNumber);
}