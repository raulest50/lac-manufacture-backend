package lacosmetics.planta.lacmanufacture.model.inventarios.real;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lacosmetics.planta.lacmanufacture.model.inventarios.Lote;
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
@Table(name = "movimiento_almacen_real")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoReal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id", unique = true, updatable = false, nullable = false)
    private int movimientoId;

    // puede ser positivo o negativo
    private double cantidad;

    // aplica para los 3 tipos de productos
    @NotNull
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // causa del movimiento
    // VENTA, COMPRA, AVERIA, USO_INTERNO, PROD_INTERNO, OTROS
    private TipoMovimiento tipo;

    /** Lote asociado a este movimiento */
    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;

    // Bidirectional relationship with OrdenCompra
    @ManyToOne
    @JoinColumn(name = "doc_ingreso_id")  // This column will hold the foreign key
    @JsonBackReference
    private DocumentoMovimiento documentoMovimiento;


    /** Enum para causas de movimiento */
    public enum TipoMovimiento {
        COMPRA, // asociado a orden de compra OCM
        BAJA, // perdida de materiales por eventos fortuitos como inundacion incendio o mismanagement
        CONSUMO, // asociado a una orden de produccion o work in progress
        BACKFLUSH, // cuando una OP se finaliza, ingreso de semiterminado o terminado
        VENTA // sale para venta producto terminado
    }


    /**
     * Constructor para se usado preferiblemente solo por
     * @param insumo
     */
    public MovimientoReal(Insumo insumo){
        cantidad = insumo.getCantidadRequerida();
        producto = insumo.getProducto();
        tipo = TipoMovimiento.USO_INTERNO;
    }

    /**
     * Constructor para ser usado preferiblemente solo por el Constructor DocumentoIngreso(OrdenCompra).
     * usarlo en otras clases solo en casos donde realmente sea muy necesario o beneficioso.
     * @param item
     */
    MovimientoReal(ItemOrdenCompra item){
        this.cantidad = item.getCantidad();
        this.producto = item.getMaterial();
        this.tipo = TipoMovimiento.COMPRA;
    }


}
