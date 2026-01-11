package exotic.app.planta.model.compras.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Clase que empaqueta los datos necesarios para hacer una busqueda de proveedores en
 * los endpoints.
 *
 * Si tipo de busqueda es ID solo id estara definido y nombre y categorias seran nulos.
 * de igual forma, si el tipo de busqueda es NOMBRE_Y_CATEGORIA, entonces id sera nulo.
 */
public class DTO_SearchProveedor {
    private String id;
    private String nombre;
    private int[] categorias;
    private SearchType searchType; // "id" or "combined"

    public enum SearchType {
        ID,
        NOMBRE_Y_CATEGORIA
    }
}
