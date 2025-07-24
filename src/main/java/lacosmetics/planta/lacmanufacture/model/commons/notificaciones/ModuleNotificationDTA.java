package lacosmetics.planta.lacmanufacture.model.commons.notificaciones;

import lacosmetics.planta.lacmanufacture.model.users.Acceso.Modulo;
import lombok.Data;

/**
 * Se genera para cada modulo. requireAtention = true indica
 * si
 *
 */
@Data
public class ModuleNotificationDTA {

    private Modulo modulo;
    private boolean requireAtention;
    private String message;

}
