package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para solicitar recomendaciones de lotes para un producto y cantidad específicos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionLotesRequestDTO {
    private String productoId;
    private double cantidad;
}