package lacosmetics.planta.lacmanufacture.model.ventas;

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
@Table(name = "facturas_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_venta_id", unique = true, updatable = false, nullable = false)
    private int facturaVentaId;

    @ManyToOne
    @JoinColumn(name = "orden_venta_id")
    private OrdenVenta ordenVenta;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @CreationTimestamp
    private LocalDateTime fechaFactura;

    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemFacturaVenta> itemsFacturaVenta = new ArrayList<>();

    private int subTotal;
    private int impuestos;
    private int totalPagar;

    private String estadoPago; // pendiente, pagada, anulada
}
