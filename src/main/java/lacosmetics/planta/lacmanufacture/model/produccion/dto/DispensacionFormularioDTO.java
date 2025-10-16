package lacosmetics.planta.lacmanufacture.model.produccion.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa un formulario de dispensación.
 * Contiene una lista de dispensaciones de materiales.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionFormularioDTO {
    private int ordenProduccionId;
    private String productoNombre;
    private List<DispensacionDTO> dispensaciones;
}
