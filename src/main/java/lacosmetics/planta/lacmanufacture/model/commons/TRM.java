package lacosmetics.planta.lacmanufacture.model.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo para representar la Tasa Representativa del Mercado (TRM)
 * de la Superintendencia Financiera de Colombia
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TRM {
    private LocalDate fecha;
    private BigDecimal valor;
    private String unidad;
    private String vigenciaDesde;
    private String vigenciaHasta;
}