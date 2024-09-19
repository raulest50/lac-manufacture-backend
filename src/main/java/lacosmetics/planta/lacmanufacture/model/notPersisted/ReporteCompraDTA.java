package lacosmetics.planta.lacmanufacture.model.notPersisted;

import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCompraDTA{

    public int idcompra;
    public LocalDate fechacompra;
    public List<MateriaPrima> productos;
}