package lacosmetics.planta.lacmanufacture.model.inventarios;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lombok.*;

import java.time.LocalDate;

/**
 * Representa un lote (batch) de material o producto terminado,
 * con referencia opcional a orden de compra o de producción.
 */
@Entity
@Table(name = "lote")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Lote {

    /** PK interno */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código único de lote (interno) */
    @Column(name = "batch_number", nullable = false, unique = true)
    private String batchNumber;

    /** Fecha de fabricación o recepción */
    @Column(name = "production_date")
    private LocalDate productionDate;

    /** Fecha de expiración, si aplica */
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /**
     * Relación opcional con la orden de compra que origina este lote.
     * Sólo uno de los dos FKs debe estar presente.
     */
    @ManyToOne
    @JoinColumn(name = "orden_compra_id")
    private OrdenCompraMateriales ordenCompraMateriales;

    /**
     * Relación opcional con la orden de producción que genera este lote de FG.
     * Sólo uno de los dos FKs debe estar presente.
     */
    @ManyToOne
    @JoinColumn(name = "orden_produccion_id")
    private OrdenProduccion ordenProduccion;

}
