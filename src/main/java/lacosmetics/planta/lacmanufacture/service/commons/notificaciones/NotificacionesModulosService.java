package lacosmetics.planta.lacmanufacture.service.commons.notificaciones;

import lacosmetics.planta.lacmanufacture.model.commons.notificaciones.ModuleNotificationDTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.users.Acceso;
import lacosmetics.planta.lacmanufacture.model.users.Acceso.Modulo;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificacionesModulosService {

    private final OrdenCompraRepo ordenCompraRepo;
    private final UserRepository userRepository;
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;

    /**
     * Verifica las notificaciones para todos los módulos a los que tiene acceso un usuario
     * @param username Nombre de usuario para el que se verifican las notificaciones
     * @return Lista de objetos con información de notificaciones por módulo
     */
    public List<ModuleNotificationDTA> checkAllNotifications4User(String username) {
        List<ModuleNotificationDTA> notifications = new ArrayList<>();

        // Buscar el usuario por su nombre de usuario
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Recorrer todos los accesos del usuario
            for (Acceso acceso : user.getAccesos()) {
                Modulo modulo = acceso.getModuloAcceso();

                // Llamar al método correspondiente según el módulo
                ModuleNotificationDTA notification = null;

                switch (modulo) {
                    case USUARIOS:
                        notification = checkNotificacionesUsuarios(user);
                        break;
                    case PRODUCTOS:
                        notification = checkNotificacionesProductos(user);
                        break;
                    case PRODUCCION:
                        notification = checkNotificacionesProduccion(user);
                        break;
                    case STOCK:
                        notification = checkNotificacionesStock(user);
                        break;
                    case PROVEEDORES:
                        notification = checkNotificacionesProveedores(user);
                        break;
                    case COMPRAS:
                        notification = checkNotificacionesCompras(user);
                        break;
                    case SEGUIMIENTO_PRODUCCION:
                        notification = checkNotificacionesSeguimientoProduccion(user);
                        break;
                    case CLIENTES:
                        notification = checkNotificacionesClientes(user);
                        break;
                    case VENTAS:
                        notification = checkNotificacionesVentas(user);
                        break;
                    case TRANSACCIONES_ALMACEN:
                        notification = checkNotificacionesTransaccionesAlmacen(user);
                        break;
                    case ACTIVOS:
                        notification = checkNotificacionesActivos(user);
                        break;
                    case CONTABILIDAD:
                        notification = checkNotificacionesContabilidad(user);
                        break;
                    case PERSONAL_PLANTA:
                        notification = checkNotificacionesPersonalPlanta(user);
                        break;
                    case BINTELLIGENCE:
                        notification = checkNotificacionesBIntelligence(user);
                        break;
                    case CARGA_MASIVA:
                        notification = checkNotificacionesCargaMasiva(user);
                        break;
                    case ADMINISTRACION_ALERTAS:
                        notification = checkNotificacionesAdministracionAlertas(user);
                        break;
                    case MASTER_CONFIGS:
                        notification = checkNotificacionesMasterConfigs(user);
                        break;
                    case CRONOGRAMA:
                        notification = checkNotificacionesCronograma(user);
                        break;
                    case ORGANIGRAMA:
                        notification = checkNotificacionesOrganigrama(user);
                        break;
                    case PAGOS_PROVEEDORES:
                        notification = checkNotificacionesPagosProveedores(user);
                        break;
                }

                if (notification != null) {
                    notifications.add(notification);
                }
            }
        }

        return notifications;
    }


    /**
     * Verifica si hay notificaciones para el módulo USUARIOS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesUsuarios(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.USUARIOS);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo PRODUCTOS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesProductos(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.PRODUCTOS);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo PRODUCCION
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesProduccion(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.PRODUCCION);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo STOCK
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesStock(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.STOCK);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo PROVEEDORES
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesProveedores(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.PROVEEDORES);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo COMPRAS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesCompras(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.COMPRAS);

        // Verificamos si hay órdenes de compra con estado 0 (pendiente liberación)
        boolean existsOrdenPendienteLiberar = ordenCompraRepo.existsByEstado(0);

        // Verificamos si hay órdenes de compra con estado 1 (pendiente envío)
        boolean existsOrdenPendienteEnviar = ordenCompraRepo.existsByEstado(1);

        // Si hay órdenes pendientes por liberar o por enviar
        if (existsOrdenPendienteLiberar || existsOrdenPendienteEnviar) {
            notification.setRequireAtention(true);

            // Personalizamos el mensaje según el caso
            if (existsOrdenPendienteLiberar && existsOrdenPendienteEnviar) {
                notification.setMessage("Hay órdenes de compra pendientes por liberar y por enviar al proveedor");
            } else if (existsOrdenPendienteLiberar) {
                notification.setMessage("Hay órdenes de compra pendientes por liberar");
            } else {
                notification.setMessage("Hay órdenes de compra pendientes por enviar al proveedor");
            }
        } else {
            // No hay órdenes pendientes
            notification.setRequireAtention(false);
            notification.setMessage("");
        }

        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo SEGUIMIENTO_PRODUCCION
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesSeguimientoProduccion(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.SEGUIMIENTO_PRODUCCION);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo CLIENTES
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesClientes(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.CLIENTES);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo VENTAS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesVentas(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.VENTAS);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo TRANSACCIONES_ALMACEN
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesTransaccionesAlmacen(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.TRANSACCIONES_ALMACEN);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo ACTIVOS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesActivos(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.ACTIVOS);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo CONTABILIDAD
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesContabilidad(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.CONTABILIDAD);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo PERSONAL_PLANTA
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesPersonalPlanta(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.PERSONAL_PLANTA);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo BINTELLIGENCE
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesBIntelligence(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.BINTELLIGENCE);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo CARGA_MASIVA
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesCargaMasiva(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.CARGA_MASIVA);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo ADMINISTRACION_ALERTAS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesAdministracionAlertas(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.ADMINISTRACION_ALERTAS);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo MASTER_CONFIGS
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesMasterConfigs(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.MASTER_CONFIGS);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo CRONOGRAMA
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesCronograma(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.CRONOGRAMA);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo ORGANIGRAMA
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesOrganigrama(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.ORGANIGRAMA);
        notification.setRequireAtention(false);
        notification.setMessage("");
        return notification;
    }

    /**
     * Verifica si hay notificaciones para el módulo PAGOS_PROVEEDORES
     * @param user Usuario para el que se verifican las notificaciones
     * @return Objeto con información de notificación
     */
    public ModuleNotificationDTA checkNotificacionesPagosProveedores(User user) {
        ModuleNotificationDTA notification = new ModuleNotificationDTA();
        notification.setModulo(Modulo.PAGOS_PROVEEDORES);

        // Verificar si hay transacciones de almacén pendientes por asentar contablemente
        // causadas por órdenes de compra de materiales
        long countPendientes = transaccionAlmacenHeaderRepo.countByEstadoContableAndTipoEntidadCausante(
            TransaccionAlmacen.EstadoContable.PENDIENTE,
            TransaccionAlmacen.TipoEntidadCausante.OCM
        );

        if (countPendientes > 0) {
            notification.setRequireAtention(true);
            notification.setMessage("Hay " + countPendientes + " transacciones de almacén pendientes por asentar contablemente");
        } else {
            notification.setRequireAtention(false);
            notification.setMessage("");
        }

        return notification;
    }

}
