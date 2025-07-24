package lacosmetics.planta.lacmanufacture.model.producto.dto.procdesigner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoDTO {
    private String productoId;
    private String nombre;
    private String tipo_producto;
    private String observaciones;
    private int costo;
    private String tipoUnidades;
    private double cantidadUnidad;
    private String fechaCreacion;
}
