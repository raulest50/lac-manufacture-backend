package lacosmetics.planta.lacmanufacture.model.dto.productos.search;

import lombok.Data;
import java.util.List;

@Data
public class ProductoSearchCriteria {
    private String search;
    private List<String> categories;
    private Integer page;
    private Integer size;
}
