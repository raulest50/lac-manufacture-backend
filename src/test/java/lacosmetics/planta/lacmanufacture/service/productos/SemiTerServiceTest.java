package lacosmetics.planta.lacmanufacture.service.productos;

import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.ManufacturingVersion;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.InsumoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.manufacturing.ManufacturingVersionRepo;
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
        ManufacturingVersionRepo manufacturingVersionRepo = mock(ManufacturingVersionRepo.class);
        TransaccionAlmacenRepo transaccionAlmacenRepo = mock(TransaccionAlmacenRepo.class);
        OrdenProduccionRepo ordenProduccionRepo = mock(OrdenProduccionRepo.class);

        SemiTerService service = new SemiTerService(
                productoRepo,
                semiTerminadoRepo,
                terminadoRepo,
                insumoRepo,
                manufacturingVersionRepo,
                transaccionAlmacenRepo,
                ordenProduccionRepo
        );

        SemiTerminado semiTerminado = new SemiTerminado();
        semiTerminado.setProductoId("S-001");

        Movimiento mov1 = new Movimiento();
        Movimiento mov2 = new Movimiento();
        OrdenProduccion op1 = new OrdenProduccion();
        OrdenProduccion op2 = new OrdenProduccion();
        Insumo insumo1 = new Insumo();
        ManufacturingVersion manufacturingVersion1 = new ManufacturingVersion();
        ManufacturingVersion manufacturingVersion2 = new ManufacturingVersion();

        when(productoRepo.findById("S-001")).thenReturn(Optional.of(semiTerminado));
        when(transaccionAlmacenRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(mov1, mov2));
        when(ordenProduccionRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(op1, op2));
        when(insumoRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(insumo1));
        when(manufacturingVersionRepo.findByProducto_ProductoId("S-001")).thenReturn(List.of(manufacturingVersion1, manufacturingVersion2));

        Map<String, Object> result = service.forceDeleteProducto("S-001");

        assertTrue((Boolean) result.get("success"));
        assertEquals("Producto eliminado correctamente", result.get("message"));
        assertEquals(2, result.get("deletedMovimientos"));
        assertEquals(2, result.get("deletedOrdenesProduccion"));
        assertEquals(1, result.get("deletedInsumos"));
        assertEquals(2, result.get("deletedManufacturingVersions"));

        verify(transaccionAlmacenRepo).deleteAll(List.of(mov1, mov2));
        verify(ordenProduccionRepo).deleteAll(List.of(op1, op2));
        verify(insumoRepo).deleteAll(List.of(insumo1));
        verify(manufacturingVersionRepo).deleteAll(List.of(manufacturingVersion1, manufacturingVersion2));
        verify(semiTerminadoRepo).deleteById("S-001");
        verifyNoInteractions(terminadoRepo);
    }

    @Test
    void forceDeleteProductoRemovesDependenciesForTerminado() {
        ProductoRepo productoRepo = mock(ProductoRepo.class);
        SemiTerminadoRepo semiTerminadoRepo = mock(SemiTerminadoRepo.class);
        TerminadoRepo terminadoRepo = mock(TerminadoRepo.class);
        InsumoRepo insumoRepo = mock(InsumoRepo.class);
        ManufacturingVersionRepo manufacturingVersionRepo = mock(ManufacturingVersionRepo.class);
        TransaccionAlmacenRepo transaccionAlmacenRepo = mock(TransaccionAlmacenRepo.class);
        OrdenProduccionRepo ordenProduccionRepo = mock(OrdenProduccionRepo.class);

        SemiTerService service = new SemiTerService(
                productoRepo,
                semiTerminadoRepo,
                terminadoRepo,
                insumoRepo,
                manufacturingVersionRepo,
                transaccionAlmacenRepo,
                ordenProduccionRepo
        );

        Terminado terminado = new Terminado();
        terminado.setProductoId("T-001");

        Movimiento movimiento = new Movimiento();
        OrdenProduccion ordenProduccion = new OrdenProduccion();
        ManufacturingVersion manufacturingVersion = new ManufacturingVersion();

        when(productoRepo.findById("T-001")).thenReturn(Optional.of(terminado));
        when(transaccionAlmacenRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of(movimiento));
        when(ordenProduccionRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of(ordenProduccion));
        when(insumoRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of());
        when(manufacturingVersionRepo.findByProducto_ProductoId("T-001")).thenReturn(List.of(manufacturingVersion));

        Map<String, Object> result = service.forceDeleteProducto("T-001");

        assertTrue((Boolean) result.get("success"));
        assertEquals("Producto eliminado correctamente", result.get("message"));
        assertEquals(1, result.get("deletedMovimientos"));
        assertEquals(1, result.get("deletedOrdenesProduccion"));
        assertEquals(0, result.get("deletedInsumos"));
        assertEquals(1, result.get("deletedManufacturingVersions"));

        verify(transaccionAlmacenRepo).deleteAll(List.of(movimiento));
        verify(ordenProduccionRepo).deleteAll(List.of(ordenProduccion));
        verify(insumoRepo, never()).deleteAll(any());
        verify(manufacturingVersionRepo).deleteAll(List.of(manufacturingVersion));
        verify(terminadoRepo).deleteById("T-001");
        verifyNoInteractions(semiTerminadoRepo);
    }
}
