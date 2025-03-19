package lacosmetics.planta.lacmanufacture.model.compras;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items_factura_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemFacturaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_compra_id", unique = true, updatable = false, nullable = false)
    private int itemCompraId;

    @ManyToOne
    @JoinColumn(name = "factura_compra_id")
    @JsonBackReference
    private FacturaCompra facturaCompra;

    @ManyToOne
    @JoinColumn(name = "materia_prima_id")
    private Material material;

    private double cantidad;

    private int precioCompra;

}
