package lacosmetics.planta.lacmanufacture.model.producto.procesos.nodo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "proceso_produccion_node")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
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



    // Enum para los tipos de handle (SOURCE o TARGET)
    public enum HandleType {
        SOURCE, TARGET
    }

    // Enum para las posiciones de handle (TOP, RIGHT, BOTTOM, LEFT)
    public enum HandlePosition {
        TOP, RIGHT, BOTTOM, LEFT
    }

}

