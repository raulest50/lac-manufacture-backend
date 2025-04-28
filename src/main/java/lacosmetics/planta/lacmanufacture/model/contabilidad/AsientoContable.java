package lacosmetics.planta.lacmanufacture.model.contabilidad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "asiento_contable")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AsientoContable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private String descripcion;
    private String modulo;           // e.g. PURCHASE, PRODUCTION
    private String documentoOrigen;  // ID de factura, orden, etc.

    @Enumerated(EnumType.STRING)
    private EstadoAsiento estado;    // BORRADOR, PUBLICADO, REVERSADO

    @OneToMany(mappedBy = "asientoContable", cascade = CascadeType.ALL)
    private List<LineaAsientoContable> lineas;

    public enum EstadoAsiento {
        BORRADOR,
        PUBLICADO,
        REVERSADO
    }

}

