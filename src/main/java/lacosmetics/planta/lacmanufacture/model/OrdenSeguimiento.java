package lacosmetics.planta.lacmanufacture.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
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

    private int seccionResponsable;

    // 0: pendiente, 1: finalizada
    private int estado;

    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinalizacion;

    @ManyToOne
    @JoinColumn(name = "orden_prod_id")
    @JsonBackReference
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private OrdenProduccion ordenProduccion;


    /**
     * Constructor para usar cuando se crea orden de seguimiento a partir de DTA
     */
    public OrdenSeguimiento(Insumo insumo, OrdenProduccion ordenProduccion) {
        this.insumo = insumo;
        this.estado=0;
        this.observaciones = "";
        Producto p = insumo.getProducto();

        if(p instanceof MateriaPrima){
            this.seccionResponsable=1; // picking, materias primas
        }
        if(p instanceof SemiTerminado){
            this.seccionResponsable = ((SemiTerminado) p).getSeccionResponsable();
        }
        if(p instanceof Terminado){
            this.seccionResponsable = ((Terminado) p).getSeccionResponsable();
        }
        //this.ordenProduccion = ordenProduccion;
    }
}
