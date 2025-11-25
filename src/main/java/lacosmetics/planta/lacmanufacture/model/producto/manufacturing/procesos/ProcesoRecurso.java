package lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proceso_recurso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proceso_id")
    private ProcesoProduccion proceso;

    @ManyToOne
    @JoinColumn(name = "recurso_id")
    private RecursoProduccion recurso;

    // Integer quantity of fixed assets required for this process
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;
    
    // Optional: Additional attributes
    private String notas; // Any special requirements or notes
}