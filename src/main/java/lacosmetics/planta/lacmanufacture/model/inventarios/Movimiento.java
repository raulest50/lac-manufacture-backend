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

    // aplica para los 3 tipos de productos
    @NotNull
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // causa del movimiento
    // VENTA, COMPRA, AVERIA, USO_INTERNO, PROD_INTERNO, OTROS
    private String tipo;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;


    // Bidirectional relationship with OrdenCompra
    @ManyToOne
    @JoinColumn(name = "doc_ingreso_id")  // This column will hold the foreign key
    @JsonBackReference
    private DocumentoMovimiento documentoMovimiento;


    public static class CausaMovimiento {
        public static final String COMPRA = "COMPRA";
        public static final String AVERIA = "AVERIA";
        public static final String USO_INTERNO = "USO_INTERNO";
        public static final String PROD_INTERNO = "PROD_INTERNO";
        public static final String SALIDA_APP_VENTAS = "SALIDA_APP_VENTAS";
    }


    /**
     * Constructor para se usado preferiblemente solo por
     * @param insumo
     */
    public Movimiento(Insumo insumo){
        cantidad = insumo.getCantidadRequerida();
        producto = insumo.getProducto();
        tipo = CausaMovimiento.USO_INTERNO;
    }

    /**
     * Constructor para ser usado preferiblemente solo por el Constructor DocumentoIngreso(OrdenCompra).
     * usarlo en otras clases solo en casos donde realmente sea muy necesario o beneficioso.
     * @param item
     */
    Movimiento(ItemOrdenCompra item){
        this.cantidad = item.getCantidad();
        this.producto = item.getMaterial();
        this.tipo = CausaMovimiento.COMPRA;
    }


}
