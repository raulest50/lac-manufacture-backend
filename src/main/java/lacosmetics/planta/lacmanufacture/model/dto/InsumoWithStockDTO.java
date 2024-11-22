package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.Data;

@Data
public class InsumoWithStockDTO {
    private int insumoId;
    private int productoId;
    private String productoNombre;
    private double cantidadRequerida;
    private double stockActual;
}
