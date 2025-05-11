package lacosmetics.planta.lacmanufacture.model.contabilidad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa un período contable para organizar los asientos contables
 * y facilitar los procesos de cierre y reportes financieros.
 */
@Entity
@Table(name = "periodo_contable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private String nombre;  // Ej: "Enero 2023", "Q1 2023", "Año Fiscal 2023"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPeriodo estado;

    @OneToMany(mappedBy = "periodoContable")
    private List<AsientoContable> asientos;

    /**
     * Estados posibles de un período contable
     */
    public enum EstadoPeriodo {
        ABIERTO,
        CERRADO
    }
}
