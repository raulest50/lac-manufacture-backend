package exotic.app.planta.model.inventarios.dto;

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
    /**
     * ID del producto terminado a ingresar al inventario.
     * Debe existir en el sistema como un producto de tipo Terminado o SemiTerminado.
     */
    private String productoId;

    /**
     * Cantidad del producto a ingresar.
     * Debe ser un valor positivo.
     */
    private double cantidad;

    /**
     * Notas adicionales sobre este ingreso de producto.
     */
    private String observaciones;

    /**
     * ID del usuario que realiza la operación.
     */
    private int usuarioId;
}
