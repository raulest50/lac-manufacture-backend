package exotic.app.planta.service.contabilidad;

import exotic.app.planta.model.contabilidad.CuentaContable;
import exotic.app.planta.repo.contabilidad.CuentaContableRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las cuentas contables (catálogo de cuentas).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaContableService {

    private final CuentaContableRepo cuentaContableRepo;

    /**
     * Obtiene todas las cuentas contables.
     * 
     * @return Lista de todas las cuentas contables
     */
    public List<CuentaContable> obtenerTodasLasCuentas() {
        log.info("Obteniendo todas las cuentas contables");
        return cuentaContableRepo.findAll();
    }

    /**
     * Obtiene una cuenta contable por su código.
     * 
     * @param codigo Código de la cuenta contable
     * @return Optional con la cuenta contable si existe, vacío en caso contrario
     */
    public Optional<CuentaContable> obtenerCuentaPorCodigo(String codigo) {
        log.info("Buscando cuenta contable con código: {}", codigo);
        return cuentaContableRepo.findById(codigo);
    }

    /**
     * Crea una nueva cuenta contable.
     * 
     * @param cuenta La cuenta contable a crear
     * @return La cuenta contable creada
     * @throws RuntimeException si ya existe una cuenta con el mismo código
     */
    /*
    @Transactional
    public CuentaContable crearCuenta(CuentaContable cuenta) {
        log.info("Creando nueva cuenta contable con código: {}", cuenta.getCodigo());

        if (cuentaContableRepo.existsById(cuenta.getCodigo())) {
            log.error("Error al crear cuenta: ya existe una cuenta con el código {}", cuenta.getCodigo());
            throw new RuntimeException("Ya existe una cuenta con el código " + cuenta.getCodigo());
        }

        return cuentaContableRepo.save(cuenta);
    }
    */

    /**
     * Actualiza una cuenta contable existente.
     * 
     * @param codigo Código de la cuenta a actualizar
     * @param cuenta Datos actualizados de la cuenta
     * @return La cuenta contable actualizada
     * @throws RuntimeException si no existe una cuenta con el código especificado
     */
    /*
    @Transactional
    public CuentaContable actualizarCuenta(String codigo, CuentaContable cuenta) {
        log.info("Actualizando cuenta contable con código: {}", codigo);

        if (!cuentaContableRepo.existsById(codigo)) {
            log.error("Error al actualizar cuenta: no existe una cuenta con el código {}", codigo);
            throw new RuntimeException("No existe una cuenta con el código " + codigo);
        }

        // Asegurar que el código no cambie
        cuenta.setCodigo(codigo);

        return cuentaContableRepo.save(cuenta);
    }
    */
}
