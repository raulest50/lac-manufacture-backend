package lacosmetics.planta.lacmanufacture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * Clase que modela cambios en el stock
 *
 *
 *
 */


@Entity
@Table(name="movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// id assigned by the database ascendig order
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    // 0: ingreso a bodega por compra etc.
    // 1: interno produccion
    // 2: salida venta
    // 3: salida perdida averias
    private int tipo_movimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Producto producto;

    @Transient
    private String productoName;

    @PostLoad
    private void postLoad() {
        if (producto != null) {
            //this.productoName = producto.getNombre();
        }
    }

}
