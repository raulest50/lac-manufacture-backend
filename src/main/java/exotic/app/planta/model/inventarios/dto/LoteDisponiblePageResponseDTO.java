package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la respuesta paginada de consulta de lotes disponibles para un producto.
 * Contiene información sobre el producto y los lotes disponibles con sus cantidades, incluyendo metadatos de paginación.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteDisponiblePageResponseDTO {
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
    
    /**
     * Número total de páginas disponibles.
     */
    private int totalPages;
    
    /**
     * Número total de elementos (lotes) disponibles.
     */
    private long totalElements;
    
    /**
     * Página actual (base 0).
     */
    private int currentPage;
    
    /**
     * Tamaño de página solicitado.
     */
    private int size;
}

