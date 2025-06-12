package lacosmetics.planta.lacmanufacture.model.inventarios;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.dto.compra.materiales.IngresoOCM_DTA;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "transaccion_almacen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionAlmacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, updatable = false, nullable = false)
    private int transaccionId;

    @OneToMany(mappedBy = "transaccionAlmacen", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Movimiento> movimientosTransaccion;

    @CreationTimestamp
    private LocalDateTime fechaTransaccion;

    /**
     * url de la foto, scan o documento fisico de soporte si lo hay
     */
    private String urlDocSoporte;

    // Bidirectional relationship with Users
    @ManyToOne
    @JoinColumn(name = "usuario_id")  // This column will hold the foreign key
    @JsonBackReference
    private User user;

    private TipoEntidadCausante tipoEntidadCausante;

    /**
     * una transaccion de almancen se compone de 1 o mas movimientos de inventario.
     * no pouede haber ningun movimiento de almancen que no este asociado a una
     * transaccion de inventario. de la misma forma ninguna transaccion de
     * inventario o almancen puede existir si no esta asociada a una entidad
     * causante, por Ej: Orden de Compra de materiales (OCM), Orden de Produccion,
     * Orden de ajuste de almance (OAA).
     */
    private int idEntidadCausante;

    private String observaciones;

    public TransaccionAlmacen(IngresoOCM_DTA ingresoOCM_dta) {

    }

    public enum TipoEntidadCausante{
        OCM, // orden de compra de materiales
        OP, // orden de produccion
        OTA, // orden de tranferencia de almacen
        OAA, // orden de ajuste de almacen
    }

}
