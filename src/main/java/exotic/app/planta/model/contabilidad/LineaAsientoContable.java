package exotic.app.planta.model.contabilidad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "linea_asiento_contable")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LineaAsientoContable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asiento_id", nullable = false)
    private AsientoContable asientoContable;

    @Column(length = 20)
    private String cuentaCodigo;     // FK a plan de cuentas

    private BigDecimal debito;       // monto al debe
    private BigDecimal credito;      // monto al haber

    private String descripcion;      // nota de la línea

}
