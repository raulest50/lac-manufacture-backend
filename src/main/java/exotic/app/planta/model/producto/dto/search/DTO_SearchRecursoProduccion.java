package exotic.app.planta.model.producto.dto.search;

import lombok.Data;

/**
 * DTO para búsqueda de recursos de producción.
 * Permite realizar búsquedas por diferentes criterios con soporte para paginación.
 */
@Data
public class DTO_SearchRecursoProduccion {

    /**
     * Tipo de búsqueda a realizar.
     */
    private TipoBusqueda tipoBusqueda;

    /**
     * Valor a buscar.
     */
    private String valorBusqueda;

    /**
     * Número de página para la paginación (0-indexed).
     */
    private Integer page;

    /**
     * Tamaño de página para la paginación.
     */
    private Integer size;

    /**
     * Enumeración para los tipos de búsqueda disponibles.
     */
    public enum TipoBusqueda {
        /**
         * Búsqueda por ID del recurso de producción.
         */
        POR_ID,

        /**
         * Búsqueda por nombre del recurso de producción (coincidencia parcial).
         */
        POR_NOMBRE
    }
}
