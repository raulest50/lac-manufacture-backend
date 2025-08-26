package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el registro de backflush (ingreso de producto terminado) sin orden de producción.
 * Permite registrar la entrada de productos terminados directamente al inventario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackflushNoPlanificadoDTO {
    private String productoId;
    private double cantidad;
    private String observaciones;
    private int usuarioId;
}