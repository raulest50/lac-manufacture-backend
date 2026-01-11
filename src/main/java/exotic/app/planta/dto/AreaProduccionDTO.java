package exotic.app.planta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir datos de Área de Producción
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaProduccionDTO {
    
    private Integer areaId;
    
    @NotBlank(message = "El nombre del área no puede estar vacío")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El responsable del área no puede ser nulo")
    private Long responsableId;
}