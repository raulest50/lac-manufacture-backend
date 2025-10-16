package lacosmetics.planta.lacmanufacture.model.produccion.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrdenProduccionDTO_save {
    private String productoId;
    private String observaciones;
    private int numeroLotes = 1; // Valor por defecto: 1 lote

    private LocalDateTime fechaLanzamiento;
    private LocalDateTime fechaFinalPlanificada;

    private String numeroPedidoComercial;
    private String areaOperativa;
    private String departamentoOperativo;

    private String loteBatchNumber;
}
