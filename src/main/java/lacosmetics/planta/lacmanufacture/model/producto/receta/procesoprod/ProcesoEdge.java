
package lacosmetics.planta.lacmanufacture.model.producto.receta.procesoprod;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="process_edges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edge_id", unique = true, updatable = false, nullable = false)
    private int edgeId;

    // Cada edge pertenece a un proceso de producción
    @ManyToOne
    @JoinColumn(name = "proceso_prod_id", nullable = false)
    private ProcesoProduccion procesoProduccion;

    // Almacena los identificadores locales de los nodos fuente y destino (para la visualización)
    @Column(name = "source_local_id", nullable = false)
    private String sourceLocalId;

    @Column(name = "target_local_id", nullable = false)
    private String targetLocalId;

    // Opcionalmente, puedes agregar una etiqueta o tipo de conexión
    private String label;
}

