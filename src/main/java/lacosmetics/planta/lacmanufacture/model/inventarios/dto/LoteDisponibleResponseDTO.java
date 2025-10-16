package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lacosmetics.planta.lacmanufacture.model.inventarios.dto.LoteRecomendadoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la respuesta de consulta de lotes disponibles para un producto.
 * Contiene información sobre el producto y los lotes disponibles con sus cantidades.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteDisponibleResponseDTO {
    /**
     * ID del producto consultado.
     */
    private String productoId;
    
    /**
     * Nombre descriptivo del producto.
     */
    private String nombreProducto;
    
    /**
     * Lista de lotes disponibles con sus cantidades y fechas.
     * Cada lote incluye información de fecha de vencimiento y cantidad disponible.
     */
    private List<LoteRecomendadoDTO> lotesDisponibles;
}