package lacosmetics.planta.lacmanufacture.model.producto.receta;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="procesos_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id", unique = true, updatable = true, nullable = false)
    private int procesoId;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "proc_node_id")
    private List<ProcesoNode> procesoNodes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mat_pri_id")
    private List<MateriaPrimaNode> materiaPrimaNodesNodes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TargetNode targetNode;

}
