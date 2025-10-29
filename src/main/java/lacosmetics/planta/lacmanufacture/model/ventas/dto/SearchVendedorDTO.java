package lacosmetics.planta.lacmanufacture.model.ventas.dto;

import lombok.Data;

/**
 * Para encapsular los datos para hacer búsqueda de un vendedor.
 */
@Data
public class SearchVendedorDTO {

    String search;
    SearchType searchType;

    public enum SearchType {
        ID,
        NAME
    }
}