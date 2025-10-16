// src/main/java/lacosmetics/planta/lacmanufacture/model/produccion/dto/InventarioEnTransitoDTO.java

package lacosmetics.planta.lacmanufacture.model.produccion.dto;

import java.util.List;

import lombok.Data;

@Data
public class InventarioEnTransitoDTO {
    private String productoId;
    private String productoNombre;
    private double cantidadTotal;
    private List<Integer> ordenesProduccionIds;
}
