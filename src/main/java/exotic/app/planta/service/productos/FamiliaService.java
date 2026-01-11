package exotic.app.planta.service.productos;

import exotic.app.planta.model.producto.Familia;
import exotic.app.planta.model.producto.Terminado;
import exotic.app.planta.repo.producto.FamiliaRepo;
import exotic.app.planta.repo.producto.TerminadoRepo;
import exotic.app.planta.resource.productos.exceptions.FamiliaExceptions.DuplicateIdException;
import exotic.app.planta.resource.productos.exceptions.FamiliaExceptions.DuplicateNameException;
import exotic.app.planta.resource.productos.exceptions.FamiliaExceptions.EmptyFieldException;
import exotic.app.planta.resource.productos.exceptions.FamiliaExceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamiliaService {

    private final FamiliaRepo familiaRepo;
    private final TerminadoRepo terminadoRepo;

    /**
     * Guarda una nueva familia o actualiza una existente, verificando que el ID y nombre sean únicos
     * @param familia La familia a guardar
     * @return La familia guardada
     * @throws EmptyFieldException si el nombre de la familia está vacío
     * @throws DuplicateIdException si ya existe una familia con el mismo ID
     * @throws DuplicateNameException si ya existe una familia con el mismo nombre
     */
    @Transactional
    public Familia saveFamilia(Familia familia) {
        log.info("Intentando guardar familia: {}", familia.getFamiliaNombre());

        // Validar que el nombre no esté vacío
        if (familia.getFamiliaNombre() == null || familia.getFamiliaNombre().trim().isEmpty()) {
            throw new EmptyFieldException("El nombre de la familia no puede estar vacío");
        }

        // Verificar si ya existe una familia con el mismo ID
        if (familia.getFamiliaId() > 0 && familiaRepo.existsById(familia.getFamiliaId())) {
            Optional<Familia> existingFamilia = familiaRepo.findById(familia.getFamiliaId());
            // Si estamos actualizando la misma familia (mismo ID), verificamos que el nombre no colisione con otra familia
            if (existingFamilia.isPresent() && !existingFamilia.get().getFamiliaNombre().equals(familia.getFamiliaNombre())) {
                if (familiaRepo.existsByFamiliaNombre(familia.getFamiliaNombre())) {
                    throw new DuplicateNameException("Ya existe una familia con el nombre: " + familia.getFamiliaNombre());
                }
            }
            // Es una actualización válida
            log.info("Actualizando familia existente con ID: {}", familia.getFamiliaId());
            return familiaRepo.save(familia);
        }

        // Es una nueva familia, verificar que el ID no exista
        if (familia.getFamiliaId() > 0 && familiaRepo.existsById(familia.getFamiliaId())) {
            throw new DuplicateIdException("Ya existe una familia con el ID: " + familia.getFamiliaId());
        }

        // Verificar que el nombre no exista
        if (familiaRepo.existsByFamiliaNombre(familia.getFamiliaNombre())) {
            throw new DuplicateNameException("Ya existe una familia con el nombre: " + familia.getFamiliaNombre());
        }

        log.info("Guardando nueva familia: {}", familia.getFamiliaNombre());
        return familiaRepo.save(familia);
    }

    /**
     * Obtiene todas las familias registradas
     * @return Lista de todas las familias
     */
    public List<Familia> getAllFamilias() {
        log.info("Obteniendo todas las familias");
        return familiaRepo.findAll();
    }

    /**
     * Elimina una familia por su ID, solo si no está siendo referenciada por ningún producto terminado
     * @param familiaId ID de la familia a eliminar
     * @return true si la familia fue eliminada, false si no se pudo eliminar porque está siendo referenciada
     * @throws ValidationException si la familia no existe
     */
    @Transactional
    public boolean deleteFamiliaById(int familiaId) {
        log.info("Intentando eliminar familia con ID: {}", familiaId);

        // Verificar si la familia existe
        Optional<Familia> familiaOpt = familiaRepo.findById(familiaId);
        if (familiaOpt.isEmpty()) {
            log.error("No se encontró familia con ID: {}", familiaId);
            throw new ValidationException("No existe familia con ID: " + familiaId);
        }

        Familia familia = familiaOpt.get();

        // Verificar si hay productos terminados que referencian esta familia
        Specification<Terminado> spec = (root, query, cb) -> 
            cb.equal(root.get("familia").get("familiaId"), familiaId);

        long count = terminadoRepo.count(spec);

        if (count > 0) {
            log.warn("No se puede eliminar la familia con ID: {} porque está siendo referenciada por {} productos terminados", 
                    familiaId, count);
            return false;
        }

        // Si no hay referencias, eliminar la familia
        familiaRepo.deleteById(familiaId);
        log.info("Familia con ID: {} eliminada exitosamente", familiaId);
        return true;
    }

    /**
     * Obtiene una familia por su ID
     * @param familiaId ID de la familia
     * @return La familia encontrada o vacío si no existe
     */
    public Optional<Familia> getFamiliaById(int familiaId) {
        return familiaRepo.findById(familiaId);
    }
}
