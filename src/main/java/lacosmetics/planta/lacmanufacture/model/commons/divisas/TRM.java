package lacosmetics.planta.lacmanufacture.model.commons.divisas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo para representar la Tasa Representativa del Mercado (TRM)
 * de la Superintendencia Financiera de Colombia
 *
 * AUN ESTA PENDIENTE USO
 *
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