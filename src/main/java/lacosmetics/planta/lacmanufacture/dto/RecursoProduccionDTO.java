package lacosmetics.planta.lacmanufacture.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transferring resource production data with quantity information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursoProduccionDTO {
    
    @NotNull(message = "El ID del recurso no puede ser nulo")
    private Long id;
    
    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad = 1; // Default value is 1
    
    // Optional fields that might be useful for the frontend
    private String nombre;
    private String descripcion;
}