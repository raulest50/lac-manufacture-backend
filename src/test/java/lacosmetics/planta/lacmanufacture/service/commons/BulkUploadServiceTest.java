package lacosmetics.planta.lacmanufacture.service.commons;

import lacosmetics.planta.lacmanufacture.config.StorageProperties;
import lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload.BulkUploadResponseDTO;
import lacosmetics.planta.lacmanufacture.model.dto.commons.bulkupload.MaterialBulkUploadMappingDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.repo.compras.FacturaCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.LoteRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenSeguimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.InsumoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class BulkUploadServiceTest {

    @Test
    void bulkUploadWithCustomMapping() throws Exception {
        // Crear un libro de Excel con columnas en orden distinto al predeterminado
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("inventario");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("DESCRIPCION");
        header.createCell(1).setCellValue("STOCK");
        header.createCell(2).setCellValue("UNI/MEDIDAS");
        header.createCell(3).setCellValue("NUEVO CODIGO");
        header.createCell(4).setCellValue("IVA");
        header.createCell(5).setCellValue("PUNTO DE REORDEN");
        header.createCell(6).setCellValue("COSTO UNITARIO DEL PRODUCTO");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue("Material A");
        row.createCell(1).setCellValue(10);
        row.createCell(2).setCellValue("KG");
        row.createCell(3).setCellValue("me001");
        row.createCell(4).setCellValue(19);
        row.createCell(5).setCellValue(5);
        row.createCell(6).setCellValue(1000);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "inventario.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray());

        // Definir mapeo personalizado
        MaterialBulkUploadMappingDTO mapping = new MaterialBulkUploadMappingDTO();
        mapping.setDescripcion(0);
        mapping.setStock(1);
        mapping.setUnidadMedida(2);
        mapping.setProductoId(3);
        mapping.setIva(4);
        mapping.setPuntoReorden(5);
        mapping.setCostoUnitario(6);

        // Crear mocks de dependencias
        TransaccionAlmacenRepo transRepo = mock(TransaccionAlmacenRepo.class);
        TransaccionAlmacenHeaderRepo transHeaderRepo = mock(TransaccionAlmacenHeaderRepo.class);
        OrdenCompraRepo ordenCompraRepo = mock(OrdenCompraRepo.class);
        FacturaCompraRepo facturaRepo = mock(FacturaCompraRepo.class);
        ProveedorRepo proveedorRepo = mock(ProveedorRepo.class);
        ProductoRepo productoRepo = mock(ProductoRepo.class);
        MaterialRepo materialRepo = mock(MaterialRepo.class);
        SemiTerminadoRepo semiTerminadoRepo = mock(SemiTerminadoRepo.class);
        TerminadoRepo terminadoRepo = mock(TerminadoRepo.class);
        InsumoRepo insumoRepo = mock(InsumoRepo.class);
        OrdenProduccionRepo ordenProduccionRepo = mock(OrdenProduccionRepo.class);
        OrdenSeguimientoRepo ordenSeguimientoRepo = mock(OrdenSeguimientoRepo.class);
        LoteRepo loteRepo = mock(LoteRepo.class);
        StorageProperties storageProperties = new StorageProperties();

        BulkUploadService service = new BulkUploadService(
                transRepo,
                transHeaderRepo,
                ordenCompraRepo,
                facturaRepo,
                proveedorRepo,
                storageProperties,
                productoRepo,
                materialRepo,
                semiTerminadoRepo,
                terminadoRepo,
                insumoRepo,
                ordenProduccionRepo,
                ordenSeguimientoRepo,
                loteRepo);

        BulkUploadResponseDTO response = service.processBulkProductUpload(file, mapping);

        assertEquals(1, response.getSuccessCount());
        verify(materialRepo).save(argThat((Material m) ->
                m.getNombre().equals("Material A") && m.getProductoId().equals("me001")));
        ArgumentCaptor<TransaccionAlmacen> captor = ArgumentCaptor.forClass(TransaccionAlmacen.class);
        verify(transHeaderRepo).save(captor.capture());
        TransaccionAlmacen ta = captor.getValue();
        assertEquals(1, ta.getMovimientosTransaccion().size());
        assertEquals(10d, ta.getMovimientosTransaccion().get(0).getCantidad());
    }
}
