package lacosmetics.planta.lacmanufacture.model.produccion.procesos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Enum para los tipos de handle (SOURCE o TARGET)
 */
enum HandleType {
    SOURCE, TARGET
}

/**
 * Enum para las posiciones de handle (TOP, RIGHT, BOTTOM, LEFT)
 */
enum HandlePosition {
    TOP, RIGHT, BOTTOM, LEFT
}

@Entity
@Table(name = "proceso_produccion_node")
@Getter
@Setter
public class ProcesoProduccionNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pNodeId;

    @ManyToOne
    @JoinColumn(name = "proceso_id")
    private ProcesoProduccion procesoProduccion;

    // xyflow-react node data
    private double posicionX;   // Posición X en el diagrama
    private double posicionY;   // Posición Y en el diagrama

    @OneToMany(mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NodeHandle> handles;
}

@Entity
@Table(name = "node_handle")
@Getter
@Setter
class NodeHandle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long handleId;

    @ManyToOne
    @JoinColumn(name = "node_id")
    private ProcesoProduccionNode node;

    private String frontendHandleId; // ID único del handle en el frontend

    @Enumerated(EnumType.STRING)
    private HandleType type; // SOURCE o TARGET

    @Enumerated(EnumType.STRING)
    private HandlePosition position; // TOP, RIGHT, BOTTOM, LEFT

    private String label; // Etiqueta descriptiva del handle
}

@Entity
@Table(name = "node_connection")
@Getter
@Setter
class NodeConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectionId;

    @ManyToOne
    @JoinColumn(name = "source_handle_id")
    private NodeHandle sourceHandle;

    @ManyToOne
    @JoinColumn(name = "target_handle_id")
    private NodeHandle targetHandle;
}
