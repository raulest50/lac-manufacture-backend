package lacosmetics.planta.lacmanufacture.model;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_compra_id", unique = true, updatable = false, nullable = false)
    private int itemCompraId;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "materia_prima_id")
    private MateriaPrima materiaPrima;

    private double cantidad;

    private int precioCompra;

}
