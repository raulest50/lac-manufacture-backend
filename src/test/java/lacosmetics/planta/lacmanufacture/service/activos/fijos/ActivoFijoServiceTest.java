package lacosmetics.planta.lacmanufacture.service.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.OrdenCompraActivo;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.repo.activos.fijos.OrdenCompraActivoRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivoFijoServiceTest {

    @Mock
    private OrdenCompraActivoRepo ordenCompraActivoRepo;

    private ActivoFijoService activoFijoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activoFijoService = new ActivoFijoService(ordenCompraActivoRepo);
    }

    @Test
    void saveOrdenCompraActivo_validOrder_returnsSavedOrder() {
        // Arrange
        OrdenCompraActivo orden = new OrdenCompraActivo();
        Proveedor proveedor = new Proveedor();
        proveedor.setId("901234567-8");
        proveedor.setNombre("Proveedor Test");

        orden.setProveedor(proveedor);
        orden.setFechaVencimiento(LocalDateTime.now().plusDays(30));
        orden.setSubTotal(1000.0);
        orden.setIva(190.0);
        orden.setCondicionPago("0"); // crédito
        orden.setTiempoEntrega("15 días");
        orden.setPlazoPago(30);

        OrdenCompraActivo savedOrden = new OrdenCompraActivo();
        savedOrden.setOrdenCompraActivoId(1);
        savedOrden.setProveedor(proveedor);
        savedOrden.setFechaVencimiento(orden.getFechaVencimiento());
        savedOrden.setSubTotal(orden.getSubTotal());
        savedOrden.setIva(orden.getIva());
        savedOrden.setTotalPagar(1190.0); // subtotal + iva
        savedOrden.setCondicionPago(orden.getCondicionPago());
        savedOrden.setTiempoEntrega(orden.getTiempoEntrega());
        savedOrden.setPlazoPago(orden.getPlazoPago());
        savedOrden.setEstado(0);

        when(ordenCompraActivoRepo.save(any(OrdenCompraActivo.class))).thenReturn(savedOrden);

        // Act
        OrdenCompraActivo result = activoFijoService.saveOrdenCompraActivo(orden);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrdenCompraActivoId());
        assertEquals(1190.0, result.getTotalPagar());
        assertEquals(0, result.getEstado());

        verify(ordenCompraActivoRepo).save(any(OrdenCompraActivo.class));
    }

    @Test
    void saveOrdenCompraActivo_missingProveedor_throwsException() {
        // Arrange
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setFechaVencimiento(LocalDateTime.now().plusDays(30));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> activoFijoService.saveOrdenCompraActivo(orden));

        assertEquals("La orden de compra debe tener un proveedor asignado", exception.getMessage());
        verify(ordenCompraActivoRepo, never()).save(any(OrdenCompraActivo.class));
    }

    @Test
    void saveOrdenCompraActivo_missingFechaVencimiento_throwsException() {
        // Arrange
        OrdenCompraActivo orden = new OrdenCompraActivo();
        Proveedor proveedor = new Proveedor();
        proveedor.setId("901234567-8");
        orden.setProveedor(proveedor);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> activoFijoService.saveOrdenCompraActivo(orden));

        assertEquals("La fecha de vencimiento es requerida", exception.getMessage());
        verify(ordenCompraActivoRepo, never()).save(any(OrdenCompraActivo.class));
    }

    @Test
    void cancelOrdenCompraActivo_validOrder_returnsUpdatedOrder() {
        // Arrange
        int ordenId = 1;
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setOrdenCompraActivoId(ordenId);
        orden.setEstado(0); // pendiente liberación

        OrdenCompraActivo canceledOrden = new OrdenCompraActivo();
        canceledOrden.setOrdenCompraActivoId(ordenId);
        canceledOrden.setEstado(-1); // cancelada

        when(ordenCompraActivoRepo.findById(ordenId)).thenReturn(java.util.Optional.of(orden));
        when(ordenCompraActivoRepo.save(any(OrdenCompraActivo.class))).thenReturn(canceledOrden);

        // Act
        OrdenCompraActivo result = activoFijoService.cancelOrdenCompraActivo(ordenId);

        // Assert
        assertNotNull(result);
        assertEquals(-1, result.getEstado());

        verify(ordenCompraActivoRepo).findById(ordenId);
        verify(ordenCompraActivoRepo).save(any(OrdenCompraActivo.class));
    }

    @Test
    void cancelOrdenCompraActivo_alreadyCanceled_throwsException() {
        // Arrange
        int ordenId = 1;
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setOrdenCompraActivoId(ordenId);
        orden.setEstado(-1); // ya cancelada

        when(ordenCompraActivoRepo.findById(ordenId)).thenReturn(java.util.Optional.of(orden));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> activoFijoService.cancelOrdenCompraActivo(ordenId));

        assertEquals("La orden de compra ya está cancelada", exception.getMessage());
        verify(ordenCompraActivoRepo).findById(ordenId);
        verify(ordenCompraActivoRepo, never()).save(any(OrdenCompraActivo.class));
    }

    @Test
    void cancelOrdenCompraActivo_advancedState_throwsException() {
        // Arrange
        int ordenId = 1;
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setOrdenCompraActivoId(ordenId);
        orden.setEstado(2); // en proceso de envío o recepción

        when(ordenCompraActivoRepo.findById(ordenId)).thenReturn(java.util.Optional.of(orden));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> activoFijoService.cancelOrdenCompraActivo(ordenId));

        assertEquals("No se puede cancelar una orden que ya está en proceso de envío o recepción", exception.getMessage());
        verify(ordenCompraActivoRepo).findById(ordenId);
        verify(ordenCompraActivoRepo, never()).save(any(OrdenCompraActivo.class));
    }
}
