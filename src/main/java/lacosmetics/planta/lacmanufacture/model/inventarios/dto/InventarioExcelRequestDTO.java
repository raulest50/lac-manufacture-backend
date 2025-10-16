package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioExcelRequestDTO {
    private List<String> categories;
    private String searchTerm;
}
