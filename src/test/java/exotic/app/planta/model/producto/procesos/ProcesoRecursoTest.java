package exotic.app.planta.model.producto.procesos;

import exotic.app.planta.model.producto.manufacturing.procesos.ProcesoProduccion;
import exotic.app.planta.model.producto.manufacturing.procesos.ProcesoRecurso;
import exotic.app.planta.model.producto.manufacturing.procesos.RecursoProduccion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProcesoRecursoTest {

    @Test
    public void testProcesoRecursoModel() {
        // Create a ProcesoProduccion
        ProcesoProduccion proceso = new ProcesoProduccion();
        proceso.setNombre("Proceso Test");
        proceso.setNivelAcceso(1);
        proceso.setSetUpTime(10.0);
        proceso.setModel(ProcesoProduccion.TimeModelType.CONSTANT);
        proceso.setConstantSeconds(20.0);

        // Create a RecursoProduccion
        RecursoProduccion recurso = new RecursoProduccion();
        recurso.setNombre("Recurso Test");
        recurso.setDescripcion("Descripción del recurso de prueba");

        // Create ProcesoRecurso with cantidad
        ProcesoRecurso procesoRecurso = new ProcesoRecurso();
        procesoRecurso.setProceso(proceso);
        procesoRecurso.setRecurso(recurso);
        procesoRecurso.setCantidad(5); // Require 5 units of this resource
        procesoRecurso.setNotas("Notas de prueba");

        // Add to proceso
        List<ProcesoRecurso> recursos = new ArrayList<>();
        recursos.add(procesoRecurso);
        proceso.setRecursosRequeridos(recursos);

        // Add to recurso
        List<ProcesoRecurso> procesos = new ArrayList<>();
        procesos.add(procesoRecurso);
        recurso.setProcesos(procesos);

        // Verify relationships
        assertNotNull(proceso.getRecursosRequeridos());
        assertEquals(1, proceso.getRecursosRequeridos().size());

        ProcesoRecurso foundProcesoRecurso = proceso.getRecursosRequeridos().get(0);
        assertEquals(5, foundProcesoRecurso.getCantidad());
        assertEquals("Recurso Test", foundProcesoRecurso.getRecurso().getNombre());

        // Verify bidirectional relationship
        assertNotNull(recurso.getProcesos());
        assertEquals(1, recurso.getProcesos().size());
        assertEquals("Proceso Test", recurso.getProcesos().get(0).getProceso().getNombre());
    }
}
