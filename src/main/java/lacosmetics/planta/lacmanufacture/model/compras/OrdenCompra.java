package lacosmetics.planta.lacmanufacture.model.compras;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_compra_id", unique = true, updatable = false, nullable = false)
    private int ordenCompraId;

    @CreationTimestamp
    private LocalDateTime fechaEmision;

    private LocalDateTime fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", referencedColumnName = "id")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemOrdenCompra> itemsOrdenCompra;

    private int subTotal;
    private int iva19;
    private int totalPagar;

    /**
     * 0: credito
     * 1: contado
     */
    private String condicionPago;

    private String tiempoEntrega;

    private int plazoPago;

    /**
     * -1: cancelada
     *  0: pendiente aprobación proveedor
     *  1: pendiente revisión precio
     *  2: pendiente conteo
     *  3: cerrada con éxito
     */
    private int estado;

    /**
     * Plain column to store the FacturaCompra ID supplied by the provider.
     */
    private Integer facturaCompraId;

}
