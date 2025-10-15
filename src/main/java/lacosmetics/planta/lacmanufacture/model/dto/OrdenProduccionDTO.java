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
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLanzamiento; // fecha planeada de autorización
    private LocalDateTime fechaFinalPlanificada; // fecha objetivo para terminar
    private int estadoOrden;        // 0: en produccion, 1: terminada
    private String observaciones;
    private int numeroLotes;        // Número de lotes a producir
    private String numeroPedidoComercial; // pedido comercial origen
    private String areaOperativa; // área operativa que ejecuta
    private String departamentoOperativo; // departamento responsable de coordinar
    private List<OrdenSeguimientoDTO> ordenesSeguimiento;
}
