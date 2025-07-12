package lacosmetics.planta.lacmanufacture.model.activos.fijos.gestion;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registro de la depreciación de los activos a lo largo del tiempo.
 */
@Entity
@Table(name = "depreciacion_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepreciacionActivo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "activo_id", nullable = false)
    private ActivoFijo activoFijo;
    
    /**
     * Fecha en que se registra la depreciación
     */
    @Column(nullable = false)
    private LocalDate fechaDepreciacion;
    
    /**
     * Monto depreciado en este período
     */
    @Column(nullable = false)
    private BigDecimal montoDepreciado;
    
    /**
     * Valor en libros después de esta depreciación
     */
    @Column(nullable = false)
    private BigDecimal valorLibroActual;
    
    /**
     * Método de depreciación utilizado
     */
    private String metodoDepreciacion;
    
    /**
     * Referencia al asiento contable generado
     */
    @OneToOne
    @JoinColumn(name = "asiento_contable_id")
    private AsientoContable asientoContable;

    public enum MetodoDepreciacion {
        SL, // STRAIGHT LINE DEPRECIATION
        DB, // DECLINING BALANCE DEPRECIATION
        SYD, // SUM YEAR DIGITS
    }
}