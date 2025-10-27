package lacosmetics.planta.lacmanufacture.model.users.dto;

import lombok.Data;

/**
 * Para encapsular los datos para hacer busqueda de un usuario.
 * El plan es que este sea usado en el front por UserPicker.
 */
@Data
public class SearchUserDTO {

    String search;
    SearchType searchType;

    public enum SearchType {
        ID,
        NAME,
        EMAIL
    }

}
