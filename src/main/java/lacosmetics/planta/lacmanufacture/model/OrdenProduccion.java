package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


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

    // 0: en produccion, 1:terminada
    private int estado_orden;

    @Lob
    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinal;

}
