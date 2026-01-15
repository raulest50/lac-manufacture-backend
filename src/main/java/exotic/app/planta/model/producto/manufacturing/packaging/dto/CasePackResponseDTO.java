package exotic.app.planta.model.producto.manufacturing.packaging.dto;

import exotic.app.planta.model.producto.manufacturing.packaging.CasePack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasePackResponseDTO {
    private Integer unitsPerCase;
    private List<InsumoEmpaqueDTO> insumosEmpaque;

    public static CasePackResponseDTO fromCasePack(CasePack casePack) {
        if (casePack == null) {
            return new CasePackResponseDTO(null, Collections.emptyList());
        }
        List<InsumoEmpaqueDTO> insumos = casePack.getInsumosEmpaque() == null
            ? Collections.emptyList()
            : casePack.getInsumosEmpaque().stream()
                .filter(Objects::nonNull)
                .map(insumo -> {
                    String materialId = insumo.getMaterial() != null
                        ? insumo.getMaterial().getProductoId()
                        : null;
                    return new InsumoEmpaqueDTO(
                        materialId,
                        insumo.getCantidad(),
                        insumo.getUom()
                    );
                })
                .collect(Collectors.toList());
        return new CasePackResponseDTO(casePack.getUnitsPerCase(), insumos);
    }
}

