package lacosmetics.planta.lacmanufacture.model.produccion;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "recurso_asignado_orden")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecursoAsignadoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    @JsonBackReference(value = "seguimiento-recurso")
    private OrdenSeguimiento ordenSeguimiento;

    @ManyToOne
    @JoinColumn(name = "activo_id")
    private ActivoFijo activoFijoAsignado;

    @ManyToOne
    @JoinColumn(name = "recurso_id")
    private RecursoProduccion recursoProduccion;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaFin;

    // Tiempos reales registrados
    private Double tiempoSetupReal; // en minutos
    private Double tiempoProcesoReal; // en minutos

    // Estado del uso del recurso
    private Integer estado; // 0: asignado, 1: en uso, 2: finalizado

}
