package lacosmetics.planta.lacmanufacture.model.produccion.procesos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.List;

public class ProcesoProduccionNode {

    @Id
    @GeneratedValue
    private String pNodeId;

    private ProcesoProduccion procesoProduccion;

    // xyflow-react node data
    private double posicionX;   // Posición X en el diagrama
    private double posicionY;   // Posición Y en el diagrama

    private List<String> inputs;
    private List<String> outputs;

}
