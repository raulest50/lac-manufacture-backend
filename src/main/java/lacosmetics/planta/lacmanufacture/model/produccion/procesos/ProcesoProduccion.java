package lacosmetics.planta.lacmanufacture.model.produccion.procesos;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lombok.*;

import java.util.List;

@Entity
@Data
public class ProcesoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id", unique = true, nullable = false)
    private int procesoId;

    private String nombre;

    private List<ActivoFijo> listaActivosFijosRequeridos;

    // modelamiento de tiempos
    private double setUpTime; // tiempo de preparacion

    private double processTime; // tiempo de ejecucion

}
