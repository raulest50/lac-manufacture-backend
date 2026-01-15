package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionAlmacenDetalleDTO {
    private int transaccionId;
    private LocalDateTime fechaTransaccion;
    private int idEntidadCausante;
    private String tipoEntidadCausante;
    private String observaciones;
    private String estadoContable;
    private TransaccionAlmacenResponseDTO.UsuarioAprobadorDTO usuarioAprobador;
    private List<MovimientoDetalleDTO> movimientos = new ArrayList<>();
}

