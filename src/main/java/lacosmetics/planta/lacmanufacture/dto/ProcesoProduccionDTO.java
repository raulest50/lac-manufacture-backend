package lacosmetics.planta.lacmanufacture.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for transferring production process data including resources with quantities
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProduccionDTO {
    
    private Integer procesoId;
    
    @NotBlank(message = "El nombre del proceso no puede estar vacío")
    private String nombre;
    
    @Valid
    private List<RecursoProduccionDTO> recursosRequeridos;
    
    @NotNull(message = "El tiempo de preparación no puede ser nulo")
    private Double setUpTime;
    
    @NotNull(message = "El tiempo de proceso no puede ser nulo")
    private Double processTime;
    
    private Integer nivelAcceso = 1; // Default value is 1 (basic access level)
}