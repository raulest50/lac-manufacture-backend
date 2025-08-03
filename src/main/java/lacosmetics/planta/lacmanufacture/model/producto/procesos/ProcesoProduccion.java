package lacosmetics.planta.lacmanufacture.model.producto.procesos;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "proceso_produccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id", unique = true, nullable = false)
    private int procesoId;

    @Column(nullable = false)
    private String nombre;

    @ManyToMany
    @JoinTable(
            name = "proceso_recurso",
            joinColumns = @JoinColumn(name = "proceso_id"),
            inverseJoinColumns = @JoinColumn(name = "recurso_id")
    )
    private List<RecursoProduccion> recursosRequeridos;

    // modelamiento de tiempos
    private double setUpTime; // tiempo de preparacion

    private double processTime; // tiempo de ejecucion

}
