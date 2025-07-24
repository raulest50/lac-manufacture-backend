package lacosmetics.planta.lacmanufacture.model.produccion;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
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
    private Producto producto;

    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "orden_prod_id")
    @OneToMany(mappedBy = "ordenProduccion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrdenSeguimiento> ordenesSeguimiento = new ArrayList<>();

    // 0: en produccion, 1:terminada
    private int estadoOrden;

    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaLanzamiento;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinal;

    public OrdenProduccion(Producto producto, String observaciones) {
        this.producto = producto;
        this.observaciones = observaciones;
        this.estadoOrden = 0;

        List<OrdenSeguimiento> ordenesSeguimiento = new ArrayList<>();
        List<Insumo> insumos = new ArrayList<>();

        if (producto instanceof Terminado) {
            Terminado terminado = (Terminado) producto;
            insumos = terminado.getInsumos();
        } else if (producto instanceof SemiTerminado) {
            SemiTerminado semiTerminado = (SemiTerminado) producto;
            insumos = semiTerminado.getInsumos();
        } else {
            throw new IllegalArgumentException("El producto debe ser Terminado o SemiTerminado");
        }

        for (Insumo insumo : insumos) {
            ordenesSeguimiento.add(new OrdenSeguimiento(insumo, this));
        }
        this.ordenesSeguimiento = ordenesSeguimiento;
    }


}
