package lacosmetics.planta.lacmanufacture.model.compras;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
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
    @JsonBackReference
    private OrdenCompra ordenCompra;

    // Bidirectional relationship with OrdenCompra
    @ManyToOne
    @JoinColumn(name = "producto_id")  // This column will hold the foreign key
    private Material material;

    private int cantidad;
    private int precioUnitario;
    private int iva19;
    private int subTotal;

    public int getPrecioUnitarioFinal(){
        return precioUnitario + iva19;
    }

    /**
     * 0: aun por revisar
     * 1: si concuerda
     * -1: no concuerda
     */
    private int cantidadCorrecta;

    /**
     * 0: aun por revisar
     * 1: si concuerda
     * -1: no concuerda
     */
    private int precioCorrecto;

}
