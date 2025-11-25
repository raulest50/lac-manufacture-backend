package lacosmetics.planta.lacmanufacture.model.producto.dto.procdef;

import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.RecursoProduccion;
import lombok.Data;

@Data
public class ReProdModDto
{
    private RecursoProduccion oldRecursoProduccion;
    private RecursoProduccion newRecursoProduccion;
}
