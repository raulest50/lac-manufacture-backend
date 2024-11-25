// src/main/java/lacosmetics/planta/lacmanufacture/model/dto/InventarioEnTransitoDTO.java

package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class InventarioEnTransitoDTO {
    private int productoId;
    private String productoNombre;
    private double cantidadTotal;
    private List<Integer> ordenesProduccionIds;
}
