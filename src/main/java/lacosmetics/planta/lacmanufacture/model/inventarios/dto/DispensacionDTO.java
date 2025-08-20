package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionDTO {
    private int ordenProduccionId;
    private String observaciones;
    private int usuarioId;
    private List<DispensacionItemDTO> items;
}