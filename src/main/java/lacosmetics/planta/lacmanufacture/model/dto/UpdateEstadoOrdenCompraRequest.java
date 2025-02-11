package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.Data;

@Data
public class UpdateEstadoOrdenCompraRequest {
    private int newEstado;
    // Optional field: used only when transitioning from estado 0 to 1
    private Integer facturaCompraId;
}
