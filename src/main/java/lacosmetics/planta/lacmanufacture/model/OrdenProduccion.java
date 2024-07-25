package lacosmetics.planta.lacmanufacture.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "ordenes_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_id", unique = true, updatable = false, nullable = false)
    private int ordenId;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Terminado terminado;

    private int seccionResponsable;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orden_prod_id")
    @JsonManagedReference
    private List<OrdenSeguimiento> ordenesSeguimiento;

    // 0: en produccion, 1:terminada
    private int estadoOrden;

    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaInicio;


    private LocalDateTime fechaFinal;

    /**
     * constructor para crear las ordenes de produccion a partir de DTA.
     * @param terminado
     * @param seccionResponsable
     * @param observaciones
     */
    public OrdenProduccion(Terminado terminado, int seccionResponsable, String observaciones) {
        this.seccionResponsable = seccionResponsable;
        this.observaciones = observaciones;
        this.estadoOrden = 0;
        this.terminado = terminado;

        List<OrdenSeguimiento> ordenesSeguimiento =  new ArrayList<>();
        for(Insumo insumo : terminado.getInsumos()){
            ordenesSeguimiento.add(new OrdenSeguimiento(insumo, this));
        }
        this.ordenesSeguimiento = ordenesSeguimiento;
    }
}
