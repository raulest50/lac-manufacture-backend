package exotic.app.planta.model.producto.dto.procdef;

import exotic.app.planta.model.producto.manufacturing.procesos.RecursoProduccion;
import lombok.Data;

@Data
public class ReProdModDto
{
    private RecursoProduccion oldRecursoProduccion;
    private RecursoProduccion newRecursoProduccion;
}
