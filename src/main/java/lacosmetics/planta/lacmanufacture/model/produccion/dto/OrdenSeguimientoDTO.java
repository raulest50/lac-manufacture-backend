// In lacosmetics.planta.lacmanufacture.model.produccion.dto.OrdenSeguimientoDTO.java

package lacosmetics.planta.lacmanufacture.model.produccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenSeguimientoDTO {
    private int seguimientoId;
    private String insumoNombre;   // insumo.producto.nombre
    private double cantidadRequerida; // insumo.cantidadRequerida
    private int estado;            // 0: pendiente, 1: finalizada
}
