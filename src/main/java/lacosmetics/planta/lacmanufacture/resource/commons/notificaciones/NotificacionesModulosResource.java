package lacosmetics.planta.lacmanufacture.resource.commons.notificaciones;

import lacosmetics.planta.lacmanufacture.model.commons.notificaciones.ModuleNotificationDTA;
import lacosmetics.planta.lacmanufacture.model.users.Acceso.Modulo;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.service.commons.notificaciones.NotificacionesModulosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionesModulosResource {

    private final NotificacionesModulosService notificacionesModulosService;


    /**
     * Endpoint para verificar notificaciones para todos los módulos a los que tiene acceso un usuario
     * @param username Nombre de usuario para el que se verifican las notificaciones
     * @return Lista de objetos con información de notificaciones por módulo
     */
    @GetMapping("/notifications4user")
    public ResponseEntity<List<ModuleNotificationDTA>> checkNotifications4User(@RequestParam String username) {
        List<ModuleNotificationDTA> notifications = notificacionesModulosService.checkAllNotifications4User(username);
        return ResponseEntity.ok(notifications);
    }


}
