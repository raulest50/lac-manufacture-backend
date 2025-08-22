package lacosmetics.planta.lacmanufacture.service.master.configs;

import lacosmetics.planta.lacmanufacture.model.master.configs.MasterDirective;
import lacosmetics.planta.lacmanufacture.model.master.configs.dto.DTO_All_MasterDirectives;
import lacosmetics.planta.lacmanufacture.model.master.configs.dto.DTO_MasterD_Update;
import lacosmetics.planta.lacmanufacture.repo.master.configs.MasterDirectiveRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las directivas maestras de configuración
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterDirectiveService {

    private final MasterDirectiveRepo masterDirectiveRepo;

    /**
     * Obtiene todas las directivas maestras
     * @return DTO con la lista de todas las directivas maestras
     */
    public DTO_All_MasterDirectives getAllMasterDirectives() {
        List<MasterDirective> masterDirectives = masterDirectiveRepo.findAll();
        return new DTO_All_MasterDirectives(masterDirectives);
    }

    /**
     * Actualiza una directiva maestra
     * @param updateDTO DTO con la directiva original y la nueva directiva
     * @return La directiva actualizada
     * @throws RuntimeException si la directiva no existe o si se intenta cambiar el nombre
     */
    public MasterDirective updateMasterDirective(DTO_MasterD_Update updateDTO) {
        MasterDirective oldDirective = updateDTO.getOldMasterDirective();
        MasterDirective newDirective = updateDTO.getNewMasterDirective();
        
        // Verificar que la directiva existe
        Optional<MasterDirective> existingDirectiveOpt = masterDirectiveRepo.findById(oldDirective.getId());
        if (existingDirectiveOpt.isEmpty()) {
            throw new RuntimeException("La directiva maestra con ID " + oldDirective.getId() + " no existe");
        }
        
        MasterDirective existingDirective = existingDirectiveOpt.get();
        
        // Verificar que no se está cambiando el nombre (que es único)
        if (!existingDirective.getNombre().equals(newDirective.getNombre())) {
            throw new RuntimeException("No se permite cambiar el nombre de una directiva maestra");
        }
        
        // Actualizar solo los campos permitidos
        existingDirective.setValor(newDirective.getValor());
        existingDirective.setResumen(newDirective.getResumen());
        existingDirective.setAyuda(newDirective.getAyuda());
        
        // Guardar y retornar la directiva actualizada
        return masterDirectiveRepo.save(existingDirective);
    }
}