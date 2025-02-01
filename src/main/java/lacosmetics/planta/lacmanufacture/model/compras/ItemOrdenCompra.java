package lacosmetics.planta.lacmanufacture.model.compras;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "item_orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", unique = true, updatable = false, nullable = false)
    private int itemOrdenId;

    // Bidirectional relationship with OrdenCompra
    @ManyToOne
    @JoinColumn(name = "orden_compra_id")  // This column will hold the foreign key
    private OrdenCompra ordenCompra;

    // Bidirectional relationship with OrdenCompra
    @ManyToOne
    @JoinColumn(name = "producto_id")  // This column will hold the foreign key
    private Producto productoId;

    //codigo interno, no del proveedor
    private int producto_codigo;

    private int cantidad;

    private int precio_unitario;

    private int iva19;

    private int subTotal;

    // true si la cantidad en la factura coincide con la cantidad en la orden de compra
    private boolean cantidadCorrecta;

    // true si el precio en la factura coincide con el precio de la orden de compra
    private boolean precioCorrecto;

}
