package exotic.app.planta.model.ventas;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import exotic.app.planta.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items_orden_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", unique = true, updatable = false, nullable = false)
    private int itemId;

    @ManyToOne
    @JoinColumn(name = "orden_venta_id")
    @JsonBackReference
    private OrdenVenta ordenVenta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private double cantidad;
    private int precioUnitario;
    private int impuestos;
}
