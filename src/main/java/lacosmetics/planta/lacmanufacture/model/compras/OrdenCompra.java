package lacosmetics.planta.lacmanufacture.model.compras;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


/*
@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
 */
public class OrdenCompra {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, updatable = false, nullable = false)
    private int ordenCompraId;

    @CreationTimestamp
    private LocalDateTime fechaEmision;

    private LocalDateTime fechaVencimiento;

    /*
    @ManyToOne
    @JoinColumn(name = "proveedor_id", referencedColumnName = "id")
    private Proveedor proveedor;
     */

    /*
    @OneToMany(mappedBy = "orden_compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemOrdenCompra> itemOrdenCompra;
     */

    private int subTotal;

    private int iva19;

    private int totalPagar;

    private String condicionPago;

    private String tiempoEntrega;

    private int plazo_pago;

}
