package lacosmetics.planta.lacmanufacture.dto.activos.fijos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para búsqueda de activos fijos disponibles para asignar a recursos de producción.
 * Permite realizar búsquedas por nombre con soporte para paginación.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_SearchActivoFijoDisponible {

    /**
     * Término de búsqueda para filtrar por nombre del activo fijo.
     */
    private String nombreBusqueda;

    /**
     * Número de página para la paginación (0-indexed).
     */
    private Integer page;

    /**
     * Tamaño de página para la paginación.
     */
    private Integer size;
}
