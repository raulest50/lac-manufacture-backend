package lacosmetics.planta.lacmanufacture.dto.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
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
     */
    private ActivoFijo.TipoActivo tipoActivo;
    
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
}