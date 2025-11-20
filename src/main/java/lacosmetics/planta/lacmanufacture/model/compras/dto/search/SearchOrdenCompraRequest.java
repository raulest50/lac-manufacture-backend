package lacosmetics.planta.lacmanufacture.model.compras.dto.search;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchOrdenCompraRequest {

    @NotBlank
    private String date1;

    @NotBlank
    private String date2;

    @NotBlank
    private String estados;

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;

    @Positive
    private Integer proveedorId;
}
