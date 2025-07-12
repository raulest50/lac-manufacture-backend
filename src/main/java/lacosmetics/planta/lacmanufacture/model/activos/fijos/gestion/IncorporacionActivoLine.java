package lacosmetics.planta.lacmanufacture.model.activos.fijos.gestion;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa una línea de detalle en un documento de incorporación de activos.
 */
@Entity
@Table(name = "incorporacion_activo_line")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncorporacionActivoLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "linea_id", unique = true, updatable = false, nullable = false)
    private Long lineaId;

    /**
     * Encabezado de incorporación al que pertenece esta línea
     */
    @ManyToOne
    @JoinColumn(name = "incorporacion_id", nullable = false)
    @JsonBackReference
    private IncorporacionActivoHeader incorporacionHeader;

    /**
     * ActivoFijo que se está incorporando
     */
    @OneToOne
    @JoinColumn(name = "activo_id")
    private ActivoFijo activoFijo;

    /**
     * Descripción del activoFijo
     */
    @Column(length = 500)
    private String descripcion;

    /**
     * Cantidad de activos de este tipo
     */
    private Integer cantidad;

    /**
     * Valor unitario del activoFijo
     */
    private BigDecimal valorUnitario;

    /**
     * Valor total (cantidad * valorUnitario)
     */
    private BigDecimal valorTotal;

    /**
     * Ubicación inicial del activoFijo
     */
    private String ubicacionInicial;

    /**
     * Vida útil en meses
     */
    private Integer vidaUtilMeses;

    /**
     * Método de depreciación
     */
    private String metodoDepreciacion;
}