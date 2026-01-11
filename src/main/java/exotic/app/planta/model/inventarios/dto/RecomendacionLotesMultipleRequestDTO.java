package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para solicitar recomendaciones de lotes para múltiples productos y cantidades.
 * Permite consultar lotes recomendados para varios items en una sola solicitud.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionLotesMultipleRequestDTO {
    private List<RecomendacionLotesRequestDTO> items;
}