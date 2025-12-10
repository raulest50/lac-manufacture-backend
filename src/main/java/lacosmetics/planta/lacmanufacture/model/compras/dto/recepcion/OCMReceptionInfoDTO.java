package lacosmetics.planta.lacmanufacture.model.compras.dto.recepcion;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;

import java.util.List;

public class OCMReceptionInfoDTO {

    public OrdenCompraMateriales ordenCompraMateriales;

    public List<TransaccionAlmacen> transaccionesAlmacen;

}
