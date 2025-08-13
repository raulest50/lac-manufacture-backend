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

    /**
     * Atributo para segnmentar
     * que proceso se pueden o no ver en la ui
     * del front end basado en el nivel de acceso
     * asignado al usuario. se trata de un quickfix
     */
    private int nivelAcceso;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcesoRecurso> recursosRequeridos;

    // modelamiento de tiempos
    private double setUpTime; // tiempo de preparacion

    private double processTime; // tiempo de ejecucion

}
