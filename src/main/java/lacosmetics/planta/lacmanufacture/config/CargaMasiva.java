package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

@Component
public class CargaMasiva {

    private final MaterialRepo materialRepo;

    // Assuming the excel files are in the "data/carga_masiva" folder relative to the working directory.
    private final String MATERIAS_PRIMAS_FILE = "data/carga_masiva/materias_primas.xlsx";
    private final String MATERIALES_EMPAQUE_FILE = "data/carga_masiva/materiales_empaque.xlsx";

    public CargaMasiva(MaterialRepo materialRepo) {
        this.materialRepo = materialRepo;
    }

    public void executeCargaMasiva() {
        CargaMateriasPrimas();
        CargaMaterialesEmpaque();
    }

    /**
     * Bulk load for materias primas (tipoMaterial = 1)
     */
    private void CargaMateriasPrimas(){
        readAndSaveMaterials(MATERIAS_PRIMAS_FILE, 1);
    }

    /**
     * Bulk load for materiales de empaque (tipoMaterial = 2)
     */
    private void CargaMaterialesEmpaque(){
        readAndSaveMaterials(MATERIALES_EMPAQUE_FILE, 2);
    }

    private void readAndSaveMaterials(String filePath, int tipoMaterial) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            // Assuming the first row is a header: NOMBRE, UNIDAD DE MEDIDA, CODIGO
            int rowCount = 0;
            // Use a common timestamp for all records in this batch if required
            LocalDateTime timestamp = LocalDateTime.now();
            for (Row row : sheet) {
                if (rowCount == 0) {
                    rowCount++; // skip header row
                    continue;
                }
                // Read cells: NOMBRE, UNIDAD DE MEDIDA, CODIGO
                Cell nombreCell = row.getCell(0);
                Cell unidadCell = row.getCell(1);
                Cell codigoCell = row.getCell(2);
                if (nombreCell == null || unidadCell == null || codigoCell == null) {
                    continue; // skip if any required cell is missing
                }
                String nombre = nombreCell.getStringCellValue().trim();
                String unidad = unidadCell.getStringCellValue().trim();
                int codigo = (int) codigoCell.getNumericCellValue();

                Material material = new Material();
                material.setProductoId(codigo);
                material.setNombre(nombre);
                material.setObservaciones("");
                material.setCosto(0);
                material.setCantidadUnidad(1);
                material.setTipoUnidades(convertUnidad(unidad));
                material.setTipoMaterial(tipoMaterial);
                // fichaTecnicaUrl remains null
                // fechaCreacion is handled automatically by @CreationTimestamp

                materialRepo.save(material);
                rowCount++;
            }
        } catch (Exception e) {
            // Ideally use a logger instead of printing to stderr
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Converts the unit of measure string to the corresponding string for tipoUnidades.
     * For example: "KG" -> "KG", "UND" -> "U", "L" -> "L".
     */
    private String convertUnidad(String unidad) {
        if (unidad.equalsIgnoreCase("KG")) {
            return "KG";
        } else if (unidad.equalsIgnoreCase("UND")) {
            return "U";
        } else if (unidad.equalsIgnoreCase("L")) {
            return "L";
        }
        // default to "KG" if the unit is not recognized
        return "KG";
    }
}
