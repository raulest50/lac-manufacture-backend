package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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