package exotic.app.planta.model.ventas.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Clase que empaqueta los datos necesarios para hacer una búsqueda de clientes en
 * los endpoints.
 *
 * Si tipo de búsqueda es ID solo id estará definido y nombre y email serán nulos.
 * De igual forma, si el tipo de búsqueda es NOMBRE_O_EMAIL, entonces id será nulo.
 */
public class DTO_SearchCliente {
    private Integer id;
    private String nombre;
    private String email;
    private SearchType searchType;

    public enum SearchType {
        ID,
        NOMBRE_O_EMAIL
    }
}
