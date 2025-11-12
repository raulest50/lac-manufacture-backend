package lacosmetics.planta.lacmanufacture.model.producto.procesos;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "proceso_produccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id", unique = true, nullable = false)
    private int procesoId;

    @Column(nullable = false)
    private String nombre;

    /**
     * Atributo para segnmentar
     * que proceso se pueden o no ver en la ui
     * del front end basado en el nivel de acceso
     * asignado al usuario. se trata de un quickfix
     */
    private int nivelAcceso;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcesoRecurso> recursosRequeridos;

    @Enumerated(EnumType.STRING)
    private TimeModelType model;

    // modelamiento de tiempos
    private double setUpTime; // tiempo de preparacion

    // CONSTANT
    private double constantSeconds;       // si model = CONSTANT

    // THROUGHPUT_RATE (unidades/segundo)
    private double throughputUnitsPerSec; // si model = THROUGHPUT_RATE

    // PER_UNIT
    private double secondsPerUnit;        // si model = PER_UNIT

    // PER_BATCH
    private double secondsPerBatch;       // si model = PER_BATCH
    private double batchSize;            // tamaño de lote

    /**
     * Para modelar el tiempo de proceso. por ejemplo el enfriamiento es el mismo tiempo
     * independiente del numero de unidades a producir. mientras que un procesos de llenado
     * de botellas de tratamiento si depende del numero de unidades. por ahora solo usare
     * los modelos Throughput y Constant pero agrego de una vez los atributos para soportar
     * los 4 modelos en caso de uso futuro.
     */
    public enum TimeModelType {
        CONSTANT,           // total = setup + constante
        THROUGHPUT_RATE,    // total = setup + (N / throughput)
        PER_UNIT,           // total = setup + (N * secondsPerUnit)
        PER_BATCH           // total = setup + ceil(N/batchSize) * secondsPerBatch
    }

}
