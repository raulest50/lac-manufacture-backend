package lacosmetics.planta.lacmanufacture.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files.
     */
    private final String UPLOAD_DIR = "data";

    /**
     * folder para guardar los datasheets de cada material. (es opcional, un material podria no tener)
     */
    private final String DS_MATERIALES = "fichas_tecnicas_mp";

    private final String PROVEEDORES = "proveedores";

    private final String PRODUCTOS = "productos";

    /**
     * Folder para guardar documentos del organigrama
     */
    private final String ORGANIGRAMA = "organigrama";

    /**
     * Folder para guardar archivos relacionados con activos fijos
     */
    private final String ACTIVOS_FIJOS = "activosFijos";

    /**
     * Folder para guardar cotizaciones de activos fijos
     */
    private final String COTIZACIONES = "Cotizaciones";

    /**
     * Folder para guardar documentos de clientes
     */
    private final String CLIENTES = "clientes";
}
