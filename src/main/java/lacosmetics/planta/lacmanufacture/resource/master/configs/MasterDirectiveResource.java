package lacosmetics.planta.lacmanufacture.resource.master.configs;

import lacosmetics.planta.lacmanufacture.model.master.configs.MasterDirective;
import lacosmetics.planta.lacmanufacture.model.master.configs.dto.DTO_All_MasterDirectives;
import lacosmetics.planta.lacmanufacture.model.master.configs.dto.DTO_MasterD_Update;
import lacosmetics.planta.lacmanufacture.service.master.configs.MasterDirectiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones con directivas maestras de configuración
 */
@RestController
@RequestMapping("/api/master-directives")
@RequiredArgsConstructor
@Slf4j
public class MasterDirectiveResource {

    private final MasterDirectiveService masterDirectiveService;

    /**
     * Endpoint para obtener todas las directivas maestras
     * @return DTO con la lista de todas las directivas maestras
     */
    @GetMapping
    public ResponseEntity<DTO_All_MasterDirectives> getAllMasterDirectives() {
        log.info("REST request para obtener todas las directivas maestras");
        DTO_All_MasterDirectives masterDirectives = masterDirectiveService.getAllMasterDirectives();
        return ResponseEntity.ok(masterDirectives);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<MasterDirective> getByNombre(@PathVariable String nombre) {
        return masterDirectiveService.getByNombre(nombre)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para actualizar una directiva maestra
     * @param updateDTO DTO con la directiva original y la nueva directiva
     * @return La directiva actualizada
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateMasterDirective(@RequestBody DTO_MasterD_Update updateDTO) {
        log.info("REST request para actualizar directiva maestra con ID: {}", 
                updateDTO.getOldMasterDirective().getId());
        
        try {
            MasterDirective updatedDirective = masterDirectiveService.updateMasterDirective(updateDTO);
            return ResponseEntity.ok(updatedDirective);
        } catch (Exception e) {
            log.error("Error al actualizar directiva maestra: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error al actualizar directiva maestra: " + e.getMessage());
        }
    }
}