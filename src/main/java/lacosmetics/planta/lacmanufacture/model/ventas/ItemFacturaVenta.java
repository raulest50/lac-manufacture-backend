package lacosmetics.planta.lacmanufacture.model.ventas;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items_factura_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemFacturaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_factura_id", unique = true, updatable = false, nullable = false)
    private int itemFacturaId;

    @ManyToOne
    @JoinColumn(name = "factura_venta_id")
    @JsonBackReference
    private FacturaVenta facturaVenta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private double cantidad;
    private int precioUnitario;
}
