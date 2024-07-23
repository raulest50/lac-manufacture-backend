package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

}
