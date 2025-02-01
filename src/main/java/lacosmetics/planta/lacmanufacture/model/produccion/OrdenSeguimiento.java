package lacosmetics.planta.lacmanufacture.model.produccion;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.Insumo;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="ordenes_seguimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seguimiento_id", unique = true, updatable = false, nullable = false)
    private int seguimientoId;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private Insumo insumo;

    // 0: pendiente, 1: finalizada
    private int estado;

    @CreationTimestamp
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinalizacion;

    //@ManyToOne
    //@JoinColumn(name = "orden_prod_id")
    //@JsonBackReference
    //@Getter(AccessLevel.NONE)
    //@Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "orden_prod_id")
    @JsonBackReference
    private OrdenProduccion ordenProduccion;


    /**
     * Constructor para usar cuando se crea orden de seguimiento a partir de DTA
     */
    public OrdenSeguimiento(Insumo insumo, OrdenProduccion ordenProduccion) {
        this.insumo = insumo;
        this.estado=0;
        Producto p = insumo.getProducto();
        this.ordenProduccion = ordenProduccion;
    }
}
