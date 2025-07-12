package lacosmetics.planta.lacmanufacture.model.produccion.procesos;


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
public class ProcesoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id", unique = true, updatable = true, nullable = false)
    private int procesoId;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Transient
    private String lugar;

    @Transient
    private List<String> listaActivos;

    @Transient
    private String Tablet;

    // modelamiento de tiempos
    @Transient
    private double setUpTime; // tiempo de preparacion

    @Transient
    private double processTime; // tiempo de ejecucion



}
