package exotic.app.planta.model.producto.dto.search;

import lombok.Data;

/**
 * DTO para búsqueda de productos terminados.
 * Este DTO se utiliza específicamente en el módulo de creación manual de órdenes de producción,
 * específicamente en el componente terminado picker.
 * Permite realizar búsquedas por diferentes criterios con soporte para paginación.
 */
@Data
public class DTO_SearchTerminado {

    /**
     * Término de búsqueda.
     */
    private String searchTerm;

    /**
     * Tipo de búsqueda a realizar.
     */
    private TipoBusqueda tipoBusqueda;

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
         * Búsqueda por ID del producto terminado.
         */
        ID,

        /**
         * Búsqueda por nombre del producto terminado (coincidencia parcial).
         */
        NOMBRE
    }
}