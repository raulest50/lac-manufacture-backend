package exotic.app.planta.service.inventarios;

import exotic.app.planta.model.inventarios.dto.InventarioExcelRequestDTO;
import exotic.app.planta.model.producto.Material;
import exotic.app.planta.model.producto.Producto;
import exotic.app.planta.model.producto.SemiTerminado;
import exotic.app.planta.model.producto.Terminado;
import exotic.app.planta.model.producto.dto.search.ProductoSearchCriteria;
import exotic.app.planta.repo.inventarios.TransaccionAlmacenRepo;
import exotic.app.planta.service.productos.ProductoService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoService productoService;
    private final TransaccionAlmacenRepo transaccionAlmacenRepo;

    public byte[] generateInventoryExcel(InventarioExcelRequestDTO dto) {
        Page<Producto> productos = productoService.consultaProductos(
                dto.getSearchTerm(),
                dto.getCategories(),
                0,
                Integer.MAX_VALUE
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nombre");
            headerRow.createCell(2).setCellValue("Categoría");
            headerRow.createCell(3).setCellValue("Stock");

            int rowIdx = 1;
            for (Producto producto : productos.getContent()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(producto.getProductoId());
                row.createCell(1).setCellValue(producto.getNombre());
                row.createCell(2).setCellValue(getCategoria(producto));
                Double stock = transaccionAlmacenRepo.findTotalCantidadByProductoId(producto.getProductoId());
                row.createCell(3).setCellValue(stock != null ? stock : 0);
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating inventory Excel", e);
        }
    }

    private String getCategoria(Producto producto) {
        if (producto instanceof Material material) {
            if (material.getTipoMaterial() == 1) {
                return ProductoSearchCriteria.CATEGORIA_MATERIA_PRIMA;
            } else if (material.getTipoMaterial() == 2) {
                return ProductoSearchCriteria.CATEGORIA_MATERIAL_EMPAQUE;
            }
        } else if (producto instanceof SemiTerminado) {
            return ProductoSearchCriteria.CATEGORIA_SEMITERMINADO;
        } else if (producto instanceof Terminado) {
            return ProductoSearchCriteria.CATEGORIA_TERMINADO;
        }
        return "";
    }
}
