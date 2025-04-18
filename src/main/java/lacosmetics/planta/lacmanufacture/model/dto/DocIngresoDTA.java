package lacosmetics.planta.lacmanufacture.model.dto;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocIngresoDTA {
    private OrdenCompra ordenCompra;
    private String user;
    private String observaciones;
}
