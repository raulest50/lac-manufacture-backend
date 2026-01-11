package exotic.app.planta.model.master.configs.dto;

import exotic.app.planta.model.master.configs.MasterDirective;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para transportar una lista de todas las directivas maestras
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_All_MasterDirectives {
    private List<MasterDirective> masterDirectives = new ArrayList<>();
}