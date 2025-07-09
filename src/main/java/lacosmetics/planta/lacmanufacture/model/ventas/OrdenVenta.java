package lacosmetics.planta.lacmanufacture.model.ventas;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_venta_id", unique = true, updatable = false, nullable = false)
    private int ordenVentaId;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "ordenVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemOrdenVenta> itemsOrdenVenta = new ArrayList<>();

    private String estado; // borrador, liberada, enviada, cerrada, cancelada

    private String condicionesPago;
    private LocalDate fechaEntregaPrevista;
}
