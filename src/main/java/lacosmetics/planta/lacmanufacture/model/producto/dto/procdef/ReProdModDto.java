package lacosmetics.planta.lacmanufacture.model.producto.dto.procdef;

import lacosmetics.planta.lacmanufacture.model.producto.procesos.RecursoProduccion;
import lombok.Data;

@Data
public class ReProdModDto
{
    private RecursoProduccion oldRecursoProduccion;
    private RecursoProduccion newRecursoProduccion;
}
