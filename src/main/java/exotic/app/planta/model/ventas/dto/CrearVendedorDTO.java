package exotic.app.planta.model.ventas.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearVendedorDTO {

    private long cedula;
    private String nombres;
    private String apellidos;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    private String email;
    private String telefono;
    private String direccion;

    private Long userId;
}
