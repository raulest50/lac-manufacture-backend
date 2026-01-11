package exotic.app.planta.service.productos;

import exotic.app.planta.model.inventarios.Movimiento;
import exotic.app.planta.model.producto.SemiTerminado;
import exotic.app.planta.model.producto.Terminado;
import exotic.app.planta.model.producto.manufacturing.receta.Insumo;
import exotic.app.planta.model.produccion.OrdenProduccion;
import exotic.app.planta.repo.inventarios.TransaccionAlmacenRepo;
import exotic.app.planta.repo.produccion.OrdenProduccionRepo;
import exotic.app.planta.repo.producto.InsumoRepo;
import exotic.app.planta.repo.producto.ProductoRepo;
import exotic.app.planta.repo.producto.SemiTerminadoRepo;
import exotic.app.planta.repo.producto.TerminadoRepo;
import exotic.app.planta.repo.producto.manufacturing.snapshots.ManufacturingVersionRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SemiTerServiceTest {

    @Test
    void forceDeleteProductoRemovesDependenciesForSemiTerminado() {
        ProductoRepo productoRepo = mock(ProductoRepo.class);
        SemiTerminadoRepo semiTerminadoRepo = mock(SemiTerminadoRepo.class);
        TerminadoRepo terminadoRepo = mock(TerminadoRepo.class);
        InsumoRepo insumoRepo = mock(InsumoRepo.class);
        TransaccionAlmacenRepo transaccionAlmacenRepo = mock(TransaccionAlmacenRepo.class);
        OrdenProduccionRepo ordenProduccionRepo = mock(OrdenProduccionRepo.class);
        ManufacturingVersionRepo manufacturingVersionRepo = mock(ManufacturingVersionRepo.class);
        ObjectMapper objectMapper = new ObjectMapper();

        SemiTerService service = new SemiTerService(
                productoRepo,
                semiTerminadoRepo,
                terminadoRepo,
                insumoRepo,
                transaccionAlmacenRepo,
                ordenProduccionRepo,
                manufacturingVersionRepo,
                objectMapper
        );

        SemiTerminado semiTerminado = new SemiTerminado();
        semiTerminado.setProductoId("S-001");

        Movimiento mov1 = new Movimiento();
        Movimiento mov2 = new Movimiento();
        OrdenProduccion op1 = new OrdenProduccion();
        OrdenProduccion op2 = new OrdenProduccion();
        Insumo insumo1 = new Insumo();

        when(productoRepo.findById("S-001")).thenReturn(Optional.of(semiTerminado));
        when(transaccionAlmacenRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(mov1, mov2));
        when(ordenProduccionRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(op1, op2));
        when(insumoRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(insumo1));

        Map<String, Object> result = service.forceDeleteProducto("S-001");

        assertTrue((Boolean) result.get("success"));
        assertEquals("Producto eliminado correctamente", result.get("message"));
        assertEquals(2, result.get("deletedMovimientos"));
        assertEquals(2, result.get("deletedOrdenesProduccion"));
        assertEquals(1, result.get("deletedInsumos"));

        verify(transaccionAlmacenRepo).deleteAll(List.of(mov1, mov2));
        verify(ordenProduccionRepo).deleteAll(List.of(op1, op2));
        verify(insumoRepo).deleteAll(List.of(insumo1));
        verify(semiTerminadoRepo).deleteById("S-001");
        verifyNoInteractions(terminadoRepo);
    }

    @Test
    void forceDeleteProductoRemovesDependenciesForTerminado() {
        ProductoRepo productoRepo = mock(ProductoRepo.class);
        SemiTerminadoRepo semiTerminadoRepo = mock(SemiTerminadoRepo.class);
        TerminadoRepo terminadoRepo = mock(TerminadoRepo.class);
        InsumoRepo insumoRepo = mock(InsumoRepo.class);
        TransaccionAlmacenRepo transaccionAlmacenRepo = mock(TransaccionAlmacenRepo.class);
        OrdenProduccionRepo ordenProduccionRepo = mock(OrdenProduccionRepo.class);
        ManufacturingVersionRepo manufacturingVersionRepo = mock(ManufacturingVersionRepo.class);
        ObjectMapper objectMapper = new ObjectMapper();

        SemiTerService service = new SemiTerService(
                productoRepo,
                semiTerminadoRepo,
                terminadoRepo,
                insumoRepo,
                transaccionAlmacenRepo,
                ordenProduccionRepo,
                manufacturingVersionRepo,
                objectMapper
        );

        Terminado terminado = new Terminado();
        terminado.setProductoId("T-001");

        Movimiento movimiento = new Movimiento();
        OrdenProduccion ordenProduccion = new OrdenProduccion();

        when(productoRepo.findById("T-001")).thenReturn(Optional.of(terminado));
        when(transaccionAlmacenRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of(movimiento));
        when(ordenProduccionRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of(ordenProduccion));
        when(insumoRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of());

        Map<String, Object> result = service.forceDeleteProducto("T-001");

        assertTrue((Boolean) result.get("success"));
        assertEquals("Producto eliminado correctamente", result.get("message"));
        assertEquals(1, result.get("deletedMovimientos"));
        assertEquals(1, result.get("deletedOrdenesProduccion"));
        assertEquals(0, result.get("deletedInsumos"));

        verify(transaccionAlmacenRepo).deleteAll(List.of(movimiento));
        verify(ordenProduccionRepo).deleteAll(List.of(ordenProduccion));
        verify(insumoRepo, never()).deleteAll(any());
        verify(terminadoRepo).deleteById("T-001");
        verifyNoInteractions(semiTerminadoRepo);
    }
}
