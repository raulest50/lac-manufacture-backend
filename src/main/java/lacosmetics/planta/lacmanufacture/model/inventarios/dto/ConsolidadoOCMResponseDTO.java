package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la respuesta del consolidado de materiales recibidos por OCM.
 * Contiene todos los materiales consolidados y el número total de transacciones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidadoOCMResponseDTO {
    private int ordenCompraId;
    private List<MaterialConsolidadoDTO> materiales;
    private int totalTransacciones;
}

