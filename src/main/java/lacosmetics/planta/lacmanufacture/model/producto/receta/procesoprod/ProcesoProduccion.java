package lacosmetics.planta.lacmanufacture.model.producto.receta.procesoprod;


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

}
