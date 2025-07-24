package lacosmetics.planta.lacmanufacture.model.inventarios;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.compras.ItemOrdenCompra;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id", unique = true, updatable = false, nullable = false)
    private int movimientoId;

    // puede ser positivo o negativo
    private double cantidad;

    @ManyToOne
    @JoinColumn(name = "transaccion_id")
    @JsonBackReference
    private TransaccionAlmacen transaccionAlmacen;

    // aplica para los 3 tipos de productos
    @NotNull
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // causa del movimiento
    // VENTA, COMPRA, AVERIA, USO_INTERNO, PROD_INTERNO, OTROS
    private TipoMovimiento tipoMovimiento;

    /**
     * lugar donde se realiza el movimiento de suma o resta de cantidades.
     * si se mueve de un almacen a otro, se resta en uno y se suma en otro
     */
    private Almacen almacen;

    /** Lote asociado a este movimiento */
    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;

    /** Enum para causas de movimiento */
    public enum TipoMovimiento {
        COMPRA, // asociado a orden de compra OCM
        BAJA, // salida de material del almancen de perdidas a eliminacion definitiva
        CONSUMO, // asociado a una orden de produccion o work in progreso
        BACKFLUSH, // cuando una OP se finaliza, ingreso de semiterminado o terminado
        VENTA, // sale para venta producto terminado
        PERDIDA, // se ingreso al almacen de perdidas.
    }

    public enum Almacen {
        GENERAL, // donde se reciben compras, se dispensa material, se ingresa backflush
        PERDIDAS, // scrap de OP's y perdidas de material por eventos fortuitos
        CALIDAD, // producto para control de calidad
        DEVOLUCIONES // terminados devuelto por clientes o materiales para devolverle a proveedor
    }

    /**
     * Constructor para se usado preferiblemente solo por
     * @param insumo
     */
    public Movimiento(Insumo insumo){
        cantidad = insumo.getCantidadRequerida();
        producto = insumo.getProducto();
        tipoMovimiento = TipoMovimiento.CONSUMO;
    }

    /**
     * Constructor para ser usado preferiblemente solo por el Constructor DocumentoIngreso(OrdenCompraMateriales).
     * usarlo en otras clases solo en casos donde realmente sea muy necesario o beneficioso.
     * @param item
     */
    Movimiento(ItemOrdenCompra item){
        this.cantidad = item.getCantidad();
        this.producto = item.getMaterial();
        this.tipoMovimiento = TipoMovimiento.COMPRA;
    }


}
