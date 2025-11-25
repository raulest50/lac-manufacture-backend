package lacosmetics.planta.lacmanufacture.model.produccion;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.RecursoProduccion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "planificacion_produccion")
@Getter
@Setter
@NoArgsConstructor
public class PlanificacionProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Double cantidadProducir;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaPlanificada;

    private Integer prioridad; // 1-alta, 2-media, 3-baja

    // Estado de la planificación
    // 0: Tentativa, 1: Confirmada, 2: En ejecución, 3: Completada, 4: Cancelada
    private Integer estado;

    // Relación con orden de producción (si ya se generó)
    @OneToOne(mappedBy = "planificacionProduccion")
    @JsonBackReference(value = "orden-planificacion")
    private OrdenProduccion ordenProduccion;

    // Campos para planificación de capacidad
    @ManyToMany
    @JoinTable(
            name = "planificacion_recurso",
            joinColumns = @JoinColumn(name = "planificacion_id"),
            inverseJoinColumns = @JoinColumn(name = "recurso_id")
    )
    private List<RecursoProduccion> recursosAsignados;


}
