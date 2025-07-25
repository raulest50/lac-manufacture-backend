package lacosmetics.planta.lacmanufacture.service.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.OrdenCompraActivo;
import lacosmetics.planta.lacmanufacture.model.compras.Proveedor;
import lacosmetics.planta.lacmanufacture.repo.activos.fijos.ItemOrdenCompraActivoRepo;
import lacosmetics.planta.lacmanufacture.repo.activos.fijos.OrdenCompraActivoRepo;
import lacosmetics.planta.lacmanufacture.service.commons.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ActivoFijoServiceTest {

    @Mock
    private OrdenCompraActivoRepo ordenCompraActivoRepo;

    @Mock
    private ItemOrdenCompraActivoRepo itemOrdenCompraActivoRepo;

    @Mock
    private FileStorageService fileStorageService;

    private ActivoFijoService activoFijoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activoFijoService = new ActivoFijoService(ordenCompraActivoRepo, itemOrdenCompraActivoRepo, fileStorageService);
    }

    @Test
    void saveOrdenCompraActivo_validOrder_returnsSavedOrder() throws Exception {
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
        savedOrden.setCotizacionUrl(""); // Empty string by default

        when(ordenCompraActivoRepo.save(any(OrdenCompraActivo.class))).thenReturn(savedOrden);

        // Act
        OrdenCompraActivo result = activoFijoService.saveOrdenCompraActivo(orden, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrdenCompraActivoId());
        assertEquals(1190.0, result.getTotalPagar());
        assertEquals(0, result.getEstado());
        assertEquals("", result.getCotizacionUrl());

        verify(ordenCompraActivoRepo).save(any(OrdenCompraActivo.class));
    }

    @Test
    void saveOrdenCompraActivo_withFile_returnsSavedOrderWithUrl() throws Exception {
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
        savedOrden.setCotizacionUrl(""); // Empty string initially

        OrdenCompraActivo updatedOrden = new OrdenCompraActivo();
        updatedOrden.setOrdenCompraActivoId(1);
        updatedOrden.setProveedor(proveedor);
        updatedOrden.setFechaVencimiento(orden.getFechaVencimiento());
        updatedOrden.setSubTotal(orden.getSubTotal());
        updatedOrden.setIva(orden.getIva());
        updatedOrden.setTotalPagar(1190.0);
        updatedOrden.setCondicionPago(orden.getCondicionPago());
        updatedOrden.setTiempoEntrega(orden.getTiempoEntrega());
        updatedOrden.setPlazoPago(orden.getPlazoPago());
        updatedOrden.setEstado(0);
        updatedOrden.setCotizacionUrl("data/activosFijos/Cotizaciones/1/cotizacion.pdf");

        // Mock file
        MultipartFile cotizacionFile = mock(MultipartFile.class);
        when(cotizacionFile.isEmpty()).thenReturn(false);

        // Mock repository and file storage
        when(ordenCompraActivoRepo.save(any(OrdenCompraActivo.class)))
            .thenReturn(savedOrden)  // First call returns savedOrden
            .thenReturn(updatedOrden); // Second call returns updatedOrden

        when(fileStorageService.storeCotizacionFile(eq(1), eq(cotizacionFile)))
            .thenReturn("data/activosFijos/Cotizaciones/1/cotizacion.pdf");

        // Act
        OrdenCompraActivo result = activoFijoService.saveOrdenCompraActivo(orden, cotizacionFile);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrdenCompraActivoId());
        assertEquals(1190.0, result.getTotalPagar());
        assertEquals(0, result.getEstado());
        assertEquals("data/activosFijos/Cotizaciones/1/cotizacion.pdf", result.getCotizacionUrl());

        verify(ordenCompraActivoRepo, times(2)).save(any(OrdenCompraActivo.class));
        verify(fileStorageService).storeCotizacionFile(eq(1), eq(cotizacionFile));
    }

    @Test
    void saveOrdenCompraActivo_missingProveedor_throwsException() throws Exception {
        // Arrange
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setFechaVencimiento(LocalDateTime.now().plusDays(30));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> activoFijoService.saveOrdenCompraActivo(orden, null));

        assertEquals("La orden de compra debe tener un proveedor asignado", exception.getMessage());
        verify(ordenCompraActivoRepo, never()).save(any(OrdenCompraActivo.class));
    }

    @Test
    void saveOrdenCompraActivo_missingFechaVencimiento_throwsException() throws Exception {
        // Arrange
        OrdenCompraActivo orden = new OrdenCompraActivo();
        Proveedor proveedor = new Proveedor();
        proveedor.setId("901234567-8");
        orden.setProveedor(proveedor);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> activoFijoService.saveOrdenCompraActivo(orden, null));

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

    @Test
    void updateOrdenCompraActivo_validOrder_returnsUpdatedOrder() throws Exception {
        // Arrange
        int ordenId = 1;
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setOrdenCompraActivoId(ordenId);

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

        OrdenCompraActivo existingOrden = new OrdenCompraActivo();
        existingOrden.setOrdenCompraActivoId(ordenId);
        existingOrden.setProveedor(proveedor);
        existingOrden.setFechaEmision(LocalDateTime.now().minusDays(5));
        existingOrden.setFechaVencimiento(LocalDateTime.now().plusDays(25));
        existingOrden.setSubTotal(900.0);
        existingOrden.setIva(171.0);
        existingOrden.setTotalPagar(1071.0);
        existingOrden.setCondicionPago("1"); // contado
        existingOrden.setTiempoEntrega("10 días");
        existingOrden.setPlazoPago(0);
        existingOrden.setEstado(0);
        existingOrden.setCotizacionUrl("data/activosFijos/Cotizaciones/1/old_cotizacion.pdf");

        OrdenCompraActivo updatedOrden = new OrdenCompraActivo();
        updatedOrden.setOrdenCompraActivoId(ordenId);
        updatedOrden.setProveedor(proveedor);
        updatedOrden.setFechaEmision(existingOrden.getFechaEmision()); // Mantener fecha original
        updatedOrden.setFechaVencimiento(orden.getFechaVencimiento());
        updatedOrden.setSubTotal(orden.getSubTotal());
        updatedOrden.setIva(orden.getIva());
        updatedOrden.setTotalPagar(1190.0);
        updatedOrden.setCondicionPago(orden.getCondicionPago());
        updatedOrden.setTiempoEntrega(orden.getTiempoEntrega());
        updatedOrden.setPlazoPago(orden.getPlazoPago());
        updatedOrden.setEstado(0);
        updatedOrden.setCotizacionUrl(existingOrden.getCotizacionUrl()); // Mantener URL original

        when(ordenCompraActivoRepo.findById(ordenId)).thenReturn(java.util.Optional.of(existingOrden));
        when(ordenCompraActivoRepo.save(any(OrdenCompraActivo.class))).thenReturn(updatedOrden);

        // Act
        OrdenCompraActivo result = activoFijoService.updateOrdenCompraActivo(orden, null);

        // Assert
        assertNotNull(result);
        assertEquals(ordenId, result.getOrdenCompraActivoId());
        assertEquals(existingOrden.getFechaEmision(), result.getFechaEmision());
        assertEquals(orden.getFechaVencimiento(), result.getFechaVencimiento());
        assertEquals(1190.0, result.getTotalPagar());
        assertEquals(existingOrden.getCotizacionUrl(), result.getCotizacionUrl());

        verify(ordenCompraActivoRepo).findById(ordenId);
        verify(ordenCompraActivoRepo).save(any(OrdenCompraActivo.class));
    }

    @Test
    void updateOrdenCompraActivo_withFile_returnsUpdatedOrderWithNewUrl() throws Exception {
        // Arrange
        int ordenId = 1;
        OrdenCompraActivo orden = new OrdenCompraActivo();
        orden.setOrdenCompraActivoId(ordenId);

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

        OrdenCompraActivo existingOrden = new OrdenCompraActivo();
        existingOrden.setOrdenCompraActivoId(ordenId);
        existingOrden.setProveedor(proveedor);
        existingOrden.setFechaEmision(LocalDateTime.now().minusDays(5));
        existingOrden.setFechaVencimiento(LocalDateTime.now().plusDays(25));
        existingOrden.setSubTotal(900.0);
        existingOrden.setIva(171.0);
        existingOrden.setTotalPagar(1071.0);
        existingOrden.setCondicionPago("1"); // contado
        existingOrden.setTiempoEntrega("10 días");
        existingOrden.setPlazoPago(0);
        existingOrden.setEstado(0);
        existingOrden.setCotizacionUrl("data/activosFijos/Cotizaciones/1/old_cotizacion.pdf");

        OrdenCompraActivo savedOrden = new OrdenCompraActivo();
        savedOrden.setOrdenCompraActivoId(ordenId);
        savedOrden.setProveedor(proveedor);
        savedOrden.setFechaEmision(existingOrden.getFechaEmision());
        savedOrden.setFechaVencimiento(orden.getFechaVencimiento());
        savedOrden.setSubTotal(orden.getSubTotal());
        savedOrden.setIva(orden.getIva());
        savedOrden.setTotalPagar(1190.0);
        savedOrden.setCondicionPago(orden.getCondicionPago());
        savedOrden.setTiempoEntrega(orden.getTiempoEntrega());
        savedOrden.setPlazoPago(orden.getPlazoPago());
        savedOrden.setEstado(0);
        savedOrden.setCotizacionUrl(existingOrden.getCotizacionUrl());

        OrdenCompraActivo updatedOrden = new OrdenCompraActivo();
        updatedOrden.setOrdenCompraActivoId(ordenId);
        updatedOrden.setProveedor(proveedor);
        updatedOrden.setFechaEmision(existingOrden.getFechaEmision());
        updatedOrden.setFechaVencimiento(orden.getFechaVencimiento());
        updatedOrden.setSubTotal(orden.getSubTotal());
        updatedOrden.setIva(orden.getIva());
        updatedOrden.setTotalPagar(1190.0);
        updatedOrden.setCondicionPago(orden.getCondicionPago());
        updatedOrden.setTiempoEntrega(orden.getTiempoEntrega());
        updatedOrden.setPlazoPago(orden.getPlazoPago());
        updatedOrden.setEstado(0);
        updatedOrden.setCotizacionUrl("data/activosFijos/Cotizaciones/1/new_cotizacion.pdf");

        // Mock file
        MultipartFile cotizacionFile = mock(MultipartFile.class);
        when(cotizacionFile.isEmpty()).thenReturn(false);

        when(ordenCompraActivoRepo.findById(ordenId)).thenReturn(java.util.Optional.of(existingOrden));
        when(ordenCompraActivoRepo.save(any(OrdenCompraActivo.class)))
            .thenReturn(savedOrden)
            .thenReturn(updatedOrden);
        when(fileStorageService.storeCotizacionFile(eq(ordenId), eq(cotizacionFile)))
            .thenReturn("data/activosFijos/Cotizaciones/1/new_cotizacion.pdf");

        // Act
        OrdenCompraActivo result = activoFijoService.updateOrdenCompraActivo(orden, cotizacionFile);

        // Assert
        assertNotNull(result);
        assertEquals(ordenId, result.getOrdenCompraActivoId());
        assertEquals(existingOrden.getFechaEmision(), result.getFechaEmision());
        assertEquals(orden.getFechaVencimiento(), result.getFechaVencimiento());
        assertEquals(1190.0, result.getTotalPagar());
        assertEquals("data/activosFijos/Cotizaciones/1/new_cotizacion.pdf", result.getCotizacionUrl());

        verify(ordenCompraActivoRepo).findById(ordenId);
        verify(ordenCompraActivoRepo, times(2)).save(any(OrdenCompraActivo.class));
        verify(fileStorageService).storeCotizacionFile(eq(ordenId), eq(cotizacionFile));
    }
}
