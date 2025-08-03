package lacosmetics.planta.lacmanufacture.model.producto.procesos.nodo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "node_connection")
@Getter
@Setter
public class NodeConnection {
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
