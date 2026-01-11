package exotic.app.planta.model.producto.manufacturing.procesos.nodo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "node_handle")
@Getter
@Setter
public class NodeHandle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long handleId;

    @ManyToOne
    @JoinColumn(name = "node_id")
    private ProcesoProduccionNode node;

    private String frontendHandleId; // ID único del handle en el frontend

    @Enumerated(EnumType.STRING)
    private ProcesoProduccionNode.HandleType type; // SOURCE o TARGET

    @Enumerated(EnumType.STRING)
    private ProcesoProduccionNode.HandlePosition position; // TOP, RIGHT, BOTTOM, LEFT

    private String label; // Etiqueta descriptiva del handle
}
