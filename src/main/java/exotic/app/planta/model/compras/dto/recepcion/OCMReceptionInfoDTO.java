package exotic.app.planta.model.compras.dto.recepcion;

import exotic.app.planta.model.compras.OrdenCompraMateriales;
import exotic.app.planta.model.inventarios.TransaccionAlmacen;

import java.util.List;

public class OCMReceptionInfoDTO {

    public OrdenCompraMateriales ordenCompraMateriales;

    public List<TransaccionAlmacen> transaccionesAlmacen;

}
