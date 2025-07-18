package lacosmetics.planta.lacmanufacture.service.organigrama;

import lacosmetics.planta.lacmanufacture.config.StorageProperties;
import lacosmetics.planta.lacmanufacture.model.organigrama.Cargo;
import lacosmetics.planta.lacmanufacture.repo.organigrama.CargoOrganigramaRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CargoOrganigramaService {

    private final CargoOrganigramaRepo cargoOrganigramaRepo;
    private final StorageProperties storageProperties;

    /**
     * Obtiene todos los cargos disponibles
     * @return Lista de todos los cargos
     */
    public List<Cargo> getAllCargos() {
        return cargoOrganigramaRepo.findAll();
    }

    /**
     * Obtiene un cargo por su ID
     * @param cargoId ID del cargo a buscar
     * @return El cargo encontrado
     * @throws IllegalArgumentException Si no existe un cargo con el ID proporcionado
     */
    public Cargo getCargoById(String cargoId) {
        return cargoOrganigramaRepo.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un Cargo con el ID: " + cargoId));
    }

    /**
     * Guarda o actualiza un cargo con su manual de funciones
     * @param cargo El cargo a guardar o actualizar
     * @param manualFuncionesFile Archivo PDF del manual de funciones (opcional)
     * @return El cargo guardado
     * @throws IOException Si ocurre un error al guardar el archivo
     */
    @Transactional
    public Cargo saveCargoWithManualFunciones(Cargo cargo, MultipartFile manualFuncionesFile) throws IOException {
        boolean isUpdate = cargo.getIdCargo() != null && !cargo.getIdCargo().isEmpty();

        // Si es una actualización, verificar si existe el cargo pero no lanzar excepción si no existe
        // En ese caso, simplemente se creará un nuevo cargo con el ID proporcionado
        if (isUpdate) {
            Optional<Cargo> cargoExistente = cargoOrganigramaRepo.findById(cargo.getIdCargo());
            // Se elimina la validación que lanzaba excepción para permitir crear cargos con ID específico
        }

        // Guardar el manual de funciones si se proporciona
        if (manualFuncionesFile != null && !manualFuncionesFile.isEmpty()) {
            String manualFuncionesPath = storeManualFunciones(cargo.getIdCargo(), manualFuncionesFile);
            cargo.setUrlDocManualFunciones(manualFuncionesPath);
        }

        // Guardar el cargo
        return cargoOrganigramaRepo.save(cargo);
    }

    /**
     * Guarda cambios en el organigrama (elimina todos los cargos y guarda los nuevos)
     * @param cargos Lista de cargos a guardar
     * @return Lista de cargos guardados
     */
    @Transactional
    public List<Cargo> saveChangesOrganigrama(List<Cargo> cargos) {
        // Eliminar todos los cargos existentes
        cargoOrganigramaRepo.deleteAll();

        // Guardar los nuevos cargos
        return cargoOrganigramaRepo.saveAll(cargos);
    }

    /**
     * Almacena el archivo PDF del manual de funciones
     * @param cargoId ID del cargo
     * @param file Archivo PDF del manual de funciones
     * @return Ruta absoluta del archivo guardado
     * @throws IOException Si ocurre un error al guardar el archivo
     */
    private String storeManualFunciones(String cargoId, MultipartFile file) throws IOException {
        // Validar que el archivo sea un PDF
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("El archivo debe ser un PDF");
        }

        // Construir la ruta del directorio: baseUploadDir/organigrama/manuales/{cargoId}
        String baseDir = storageProperties.getUPLOAD_DIR();
        String organigramaDir = storageProperties.getORGANIGRAMA();
        String manualesDir = "manuales";
        Path folderPath = Paths.get(baseDir, organigramaDir, manualesDir, cargoId);
        Files.createDirectories(folderPath); // Asegurar que el directorio existe

        // Nombre del archivo: manual_funciones.pdf
        String fileName = "manual_funciones.pdf";
        Path destinationPath = folderPath.resolve(fileName);

        // Copiar el archivo
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return destinationPath.toAbsolutePath().toString();
    }
}
