package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item de ajuste de inventario que permite incrementar o disminuir cantidades.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AjusteItemDTO {

    /** Identificador del producto a ajustar. */
    private String productoId;

    /** Cantidad del ajuste, puede ser positiva o negativa. */
    private double cantidad;

    /** Almacén en el que se aplica el ajuste. */
    private Movimiento.Almacen almacen;

    /** Lote opcional asociado al ajuste. */
    private Integer loteId;

    /** Motivo del ajuste, alineado con {@link Movimiento.TipoMovimiento} si aplica. */
    private String motivo;
}
