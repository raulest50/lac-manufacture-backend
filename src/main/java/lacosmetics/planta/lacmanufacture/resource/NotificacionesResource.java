package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.service.NotificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionesResource {

    private final NotificacionesService notificacionesService;

    /**
     * Endpoint que verifica si existen órdenes de compra pendientes por liberar
     * @return 1 si existe al menos una orden pendiente, 0 en caso contrario
     */
    @GetMapping("/checkNotificacionesCompras")
    public ResponseEntity<Integer> checkNotificacionesCompras() {
        int resultado = notificacionesService.checkOrdenesComprasPendientes();
        return ResponseEntity.ok(resultado);
    }
}
