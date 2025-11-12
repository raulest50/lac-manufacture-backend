package lacosmetics.planta.lacmanufacture.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccion.TimeModelType;
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

    @NotNull(message = "El modelo de tiempo no puede ser nulo")
    private TimeModelType model;

    // CONSTANT model
    private Double constantSeconds;

    // THROUGHPUT_RATE model
    private Double throughputUnitsPerSec;

    // PER_UNIT model
    private Double secondsPerUnit;

    // PER_BATCH model
    private Double secondsPerBatch;
    private Double batchSize;

    private Integer nivelAcceso = 1; // Default value is 1 (basic access level)
}
