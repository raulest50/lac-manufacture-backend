package lacosmetics.planta.lacmanufacture.model.compras;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas_compras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_compra_id", unique = true, updatable = false, nullable = false)
    private int facturaCompraId;

    /**
     * Reference to the supplier (Proveedor) using the surrogate key.
     * This relationship uses the internal pk field rather than the business identifier
     * to maintain referential integrity even if the business ID changes.
     */
    @ManyToOne
    @JoinColumn(name = "proveedor_pk", referencedColumnName = "pk")
    private Proveedor proveedor;

    @CreationTimestamp
    private LocalDateTime fechaCompra;

    // Note: mappedBy must match the field name in ItemFacturaCompra
    @OneToMany(mappedBy = "facturaCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemFacturaCompra> itemsCompra = new ArrayList<>();

    private int subTotal;
    private int iva19;
    private int totalPagar;

    /**
     * 0: credito
     * 1: contado
     */
    private String condicionPago;

    // en días
    private int plazoPago;

    /**
     * 0: pendiente pago
     * 1: pagada
     */
    private int estadoPago;
}
