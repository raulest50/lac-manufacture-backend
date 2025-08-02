package lacosmetics.planta.lacmanufacture.model.producto.procesos;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.*;

import java.util.List;

@Entity
@Table(name="procesos_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProduccionCompleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_completo_id", unique = true, updatable = true, nullable = false)
    private int procesoCompletoId;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "proceso_completo_id")
    private List<ProcesoProduccionNode> procesosProduccion;


}
