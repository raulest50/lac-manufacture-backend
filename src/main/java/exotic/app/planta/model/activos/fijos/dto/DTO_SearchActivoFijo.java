package exotic.app.planta.model.activos.fijos.dto;

import exotic.app.planta.model.activos.fijos.ActivoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para búsqueda de activos fijos.
 * Permite realizar búsquedas por diferentes criterios.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_SearchActivoFijo {

    /**
     * Tipo de búsqueda a realizar.
     */
    private TipoBusqueda tipoBusqueda;

    /**
     * Valor a buscar.
     */
    private String valorBusqueda;

    /**
     * Tipo de activo a filtrar (opcional).
     * @deprecated Usar tipoActivoBusqueda en su lugar para mayor flexibilidad
     */
    @Deprecated
    private ActivoFijo.TipoActivo tipoActivo;

    /**
     * Tipo de activo para la búsqueda, incluyendo la opción TODOS.
     */
    private TipoActivoBusqueda tipoActivoBusqueda;

    /**
     * Indica si se debe buscar solo activos activos.
     */
    private Boolean soloActivos;

    /**
     * Enumeración para los tipos de búsqueda disponibles.
     */
    public enum TipoBusqueda {
        /**
         * Búsqueda por ID del activo.
         */
        POR_ID,

        /**
         * Búsqueda por nombre del activo.
         */
        POR_NOMBRE,

        /**
         * Búsqueda por ubicación del activo.
         */
        POR_UBICACION,

        /**
         * Búsqueda por ID del responsable.
         */
        POR_RESPONSABLE,

        /**
         * Búsqueda por marca (brand) del activo.
         */
        POR_MARCA,

        /**
         * Búsqueda por capacidad del activo.
         */
        POR_CAPACIDAD
    }

    /**
     * Enumeración para los tipos de activos en búsquedas.
     * Incluye los tipos de activos existentes más la opción TODOS.
     */
    public enum TipoActivoBusqueda {
        /**
         * Activos de producción.
         */
        PRODUCCION,

        /**
         * Activos de mobiliario.
         */
        MOBILIARIO,

        /**
         * Activos de equipo.
         */
        EQUIPO,

        /**
         * Todos los tipos de activos.
         */
        TODOS
    }
}
