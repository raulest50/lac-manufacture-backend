package lacosmetics.planta.lacmanufacture.model.compras;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
@Entity
@Table(name = "item_orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
*/
public class ItemOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", unique = true, updatable = false, nullable = false)
    private int itemOrdenId;

    private int codigo_inter_producto;

    private int cantidad;

    private int precio_unitario;

    private int iva19;

    private int subTotal;

}
