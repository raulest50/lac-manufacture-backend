package lacosmetics.planta.lacmanufacture.service.inventarios;

import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.LoteRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.service.contabilidad.ContabilidadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MovimientosServiceTest {

    @Test
    void getStockOf2ReturnsCorrectStock() {
        TransaccionAlmacenRepo transRepo = Mockito.mock(TransaccionAlmacenRepo.class);
        ProductoRepo prodRepo = Mockito.mock(ProductoRepo.class);
        TransaccionAlmacenHeaderRepo headerRepo = Mockito.mock(TransaccionAlmacenHeaderRepo.class);
        OrdenCompraRepo ordenRepo = Mockito.mock(OrdenCompraRepo.class);
        SemiTerminadoRepo semiRepo = Mockito.mock(SemiTerminadoRepo.class);
        TerminadoRepo termRepo = Mockito.mock(TerminadoRepo.class);
        MaterialRepo materialRepo = Mockito.mock(MaterialRepo.class);
        LoteRepo loteRepo = Mockito.mock(LoteRepo.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        ContabilidadService contService = Mockito.mock(ContabilidadService.class);

        MovimientosService service = new MovimientosService(transRepo, prodRepo, headerRepo,
                ordenRepo, semiRepo, termRepo, materialRepo,
                loteRepo, userRepo, contService);

        Material producto = new Material();
        producto.setProductoId("P1");

        when(prodRepo.findByProductoId("P1")).thenReturn(Optional.of(producto));

        Movimiento m1 = new Movimiento();
        m1.setCantidad(5);
        m1.setProducto(producto);

        Movimiento m2 = new Movimiento();
        m2.setCantidad(3);
        m2.setProducto(producto);

        when(transRepo.findByProducto_ProductoId("P1")).thenReturn(List.of(m1, m2));

        Optional<ProductoStockDTO> result = service.getStockOf2("P1");

        assertTrue(result.isPresent());
        assertEquals(8.0, result.get().getStock());
        assertEquals("P1", result.get().getProducto().getProductoId());
    }
}
