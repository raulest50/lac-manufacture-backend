package exotic.app.planta.service.commons;

import exotic.app.planta.config.StorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final StorageProperties storageProperties;

    public FileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    /**
     * Stores a file for a cliente.
     * Files are saved under {baseUploadDir}/clientes/{clienteId}/.
     *
     * @param clienteId the id of the cliente.
     * @param file      the file to store.
     * @param fileName  the name to give the stored file (e.g. "rut.pdf" or "camara.pdf").
     * @return the absolute path of the stored file.
     * @throws IOException if an I/O error occurs.
     */
    public String storeFileCliente(int clienteId, MultipartFile file, String fileName) throws IOException {
        // Build the directory path for clientes: baseUploadDir/clientes/{clienteId}
        String baseDir = storageProperties.getUPLOAD_DIR();
        String clientesDir = storageProperties.getCLIENTES();
        Path folderPath = Paths.get(baseDir, clientesDir, String.valueOf(clienteId));
        Files.createDirectories(folderPath); // Ensure the directory exists

        Path destinationPath = folderPath.resolve(fileName);
        // Use Files.copy instead of transferTo for more robust behavior across environments
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toAbsolutePath().toString();
    }

    /**
     * Stores a file for a proveedor.
     * Files are saved under {baseUploadDir}/proveedores/{proveedorId}/.
     *
     * @param proveedorId the id of the proveedor.
     * @param file        the file to store.
     * @param fileName    the name to give the stored file (e.g. "rut.pdf" or "camara.pdf").
     * @return the absolute path of the stored file.
     * @throws IOException if an I/O error occurs.
     */
    public String storeFileProveedor(String proveedorId, MultipartFile file, String fileName) throws IOException {
        // Build the directory path for proveedores: baseUploadDir/proveedores/{proveedorId}
        String baseDir = storageProperties.getUPLOAD_DIR();
        String provedoresDir = storageProperties.getPROVEEDORES();
        Path folderPath = Paths.get(baseDir, provedoresDir, proveedorId);
        Files.createDirectories(folderPath); // Ensure the directory exists

        Path destinationPath = folderPath.resolve(fileName);
        // Use Files.copy instead of transferTo for more robust behavior across environments
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toAbsolutePath().toString();
    }

    /**
     * Stores the ficha técnica PDF for a Material.
     * Files are saved under {baseUploadDir}/fichas_tecnicas_mp/.
     * A unique file name is generated to avoid conflicts.
     *
     * @param file the ficha técnica file.
     * @return the absolute path of the stored file.
     * @throws IOException if an I/O error occurs.
     */
    public String storeFichaTecnica(MultipartFile file) throws IOException {
        String baseDir = storageProperties.getUPLOAD_DIR();
        String ftecs_dir = storageProperties.getDS_MATERIALES(); // folder for storing datasheets
        Path folderPath = Paths.get(baseDir, ftecs_dir);
        Files.createDirectories(folderPath);

        String originalFilename = file.getOriginalFilename();
        String newFilename = java.util.UUID.randomUUID().toString() + "_" + originalFilename;
        Path destinationPath = folderPath.resolve(newFilename);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toAbsolutePath().toString();
    }

    /**
     * Stores a quotation file for an OrdenCompraActivo.
     * Files are saved under {baseUploadDir}/activosFijos/Cotizaciones/{ordenCompraActivoId}/.
     *
     * @param ordenCompraActivoId the id of the orden compra activo.
     * @param file the quotation file to store.
     * @return the absolute path of the stored file.
     * @throws IOException if an I/O error occurs.
     */
    public String storeCotizacionFile(int ordenCompraActivoId, MultipartFile file) throws IOException {
        // Build the directory path: baseUploadDir/activosFijos/Cotizaciones/{ordenCompraActivoId}
        String baseDir = storageProperties.getUPLOAD_DIR();
        String activosFijosDir = storageProperties.getACTIVOS_FIJOS();
        String cotizacionesDir = storageProperties.getCOTIZACIONES();
        Path folderPath = Paths.get(baseDir, activosFijosDir, cotizacionesDir, String.valueOf(ordenCompraActivoId));
        Files.createDirectories(folderPath); // Ensure the directory exists

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".pdf";
        String fileName = "cotizacion" + extension;

        Path destinationPath = folderPath.resolve(fileName);
        // Use Files.copy instead of transferTo for more robust behavior across environments
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toAbsolutePath().toString();
    }

    /**
     * Stores an invoice file for a FacturaCompraActivo.
     * Files are saved under {baseUploadDir}/activosFijos/Facturas/{facturaId}/.
     *
     * @param facturaId id of the factura
     * @param file the invoice file to store
     * @return the absolute path of the stored file
     * @throws IOException if an I/O error occurs
     */
    public String storeFacturaActivoFile(int facturaId, MultipartFile file) throws IOException {
        String baseDir = storageProperties.getUPLOAD_DIR();
        String activosFijosDir = storageProperties.getACTIVOS_FIJOS();
        String facturasDir = storageProperties.getFACTURAS_AF();
        Path folderPath = Paths.get(baseDir, activosFijosDir, facturasDir, String.valueOf(facturaId));
        Files.createDirectories(folderPath);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf('.'))
            : ".pdf";
        String fileName = "factura" + extension;
        Path destinationPath = folderPath.resolve(fileName);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toAbsolutePath().toString();
    }
}
