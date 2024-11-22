package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrdenProduccionDTO {
    private int productoId;
    private int responsableId;
    private String observaciones;
}
