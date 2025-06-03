package lacosmetics.planta.lacmanufacture.service.commons;

import lacosmetics.planta.lacmanufacture.config.StorageProperties;
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
}
