package lacosmetics.planta.lacmanufacture.model.master.configs.dto;

import lacosmetics.planta.lacmanufacture.model.master.configs.MasterDirective;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizar una directiva maestra
 * Contiene la directiva original y la nueva directiva con los cambios
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_MasterD_Update {
    private MasterDirective oldMasterDirective;
    private MasterDirective newMasterDirective;
}