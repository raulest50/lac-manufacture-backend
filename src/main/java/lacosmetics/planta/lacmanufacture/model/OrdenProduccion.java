package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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
    private int orden_id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Terminado terminado;

    private int seccion_responsable;

    @OneToMany
    @JoinColumn(name = "orden_prod_id")
    private List<OrdenSeguimiento> ordenesSeguimiento;

    // 0: en produccion, 1:terminada
    private int estado_orden;

    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaInicio;


    private LocalDateTime fechaFinal;

}
