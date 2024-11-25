// In lacosmetics.planta.lacmanufacture.model.dto.OrdenProduccionDTO.java

package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenProduccionDTO {
    private int ordenId;
    private String productoNombre;  // producto.nombre
    private LocalDateTime fechaInicio;
    private int estadoOrden;        // 0: en produccion, 1: terminada
    private int responsableId;
    private String observaciones;
    private List<OrdenSeguimientoDTO> ordenesSeguimiento;
}
