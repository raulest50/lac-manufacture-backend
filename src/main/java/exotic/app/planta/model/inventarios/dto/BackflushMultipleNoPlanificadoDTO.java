package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para el registro de backflush múltiple (ingreso de productos terminados) sin orden de producción.
 * Permite registrar la entrada de múltiples productos terminados directamente al inventario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackflushMultipleNoPlanificadoDTO {
    private String observaciones;
    private int usuarioId;
    private List<BackflushNoPlanificadoItemDTO> items;
}