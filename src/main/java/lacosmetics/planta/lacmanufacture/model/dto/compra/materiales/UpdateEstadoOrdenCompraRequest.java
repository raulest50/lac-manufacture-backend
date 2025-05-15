package lacosmetics.planta.lacmanufacture.model.dto.compra.materiales;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateEstadoOrdenCompraRequest {
    private int newEstado;
    private MultipartFile OCMpdf;
}
