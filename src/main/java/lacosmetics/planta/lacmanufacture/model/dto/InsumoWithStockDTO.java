package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class InsumoWithStockDTO {
    private int insumoId;
    private String productoId;
    private String productoNombre;
    private double cantidadRequerida;
    private double stockActual;
    private TipoProducto tipoProducto; // Nuevo campo para el tipo de producto
    private String tipoUnidades; // Unidad de medida (KG, L, U, etc.)

    // Nueva lista para almacenar los insumos de un semielaborado
    private List<InsumoWithStockDTO> subInsumos = new ArrayList<>();

    // Enum para representar los tipos de productos
    public enum TipoProducto {
        M,      // Material
        S,      // Semielaborado
        T       // Terminado
    }
}
