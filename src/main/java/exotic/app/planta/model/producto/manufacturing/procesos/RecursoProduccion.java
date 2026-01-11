package exotic.app.planta.model.producto.manufacturing.procesos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import exotic.app.planta.model.activos.fijos.ActivoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecursoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Marmitas", "Mezcladoras"

    private String descripcion;

    @OneToMany(mappedBy = "tipoRecurso")
    @JsonManagedReference(value = "recurso-activo")
    private List<ActivoFijo> activosFijos;

    // Atributos para planificación de capacidad
    private Double capacidadTotal; // Suma de capacidades de todos los activos
    private Integer cantidadDisponible; // Número de activos disponibles

    // *** ATRIBUTOS EXPERIMENTALES
    private Double capacidadPorHora; // Unidades procesables por hora
    private Integer turnos; // Número de turnos operativos
    private Double horasPorTurno; // Horas efectivas por turno

    @OneToMany(mappedBy = "recurso")
    private List<ProcesoRecurso> procesos;

}
