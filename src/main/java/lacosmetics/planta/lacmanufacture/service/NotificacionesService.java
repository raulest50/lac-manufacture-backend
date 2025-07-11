package lacosmetics.planta.lacmanufacture.service;

import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacionesService {

    private final OrdenCompraRepo ordenCompraRepo;

    /**
     * Verifica si existe al menos una orden de compra pendiente por liberar (estado = 0)
     * @return 1 si existe al menos una orden pendiente, 0 en caso contrario
     */
    public int checkOrdenesComprasPendientes() {
        // Utilizamos una consulta optimizada que solo verifica la existencia
        // sin necesidad de cargar objetos completos
        boolean existsOrdenPendiente = ordenCompraRepo.existsByEstado(0);

        // Retornamos 1 si existe al menos una orden pendiente, 0 en caso contrario
        return existsOrdenPendiente ? 1 : 0;
    }
}
