package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.master.configs.MasterDirective;
import lacosmetics.planta.lacmanufacture.repo.master.configs.MasterDirectiveRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MasterDirectiveInitializer {

    private final MasterDirectiveRepo masterDirectiveRepo;

    /**
     * Inicializa la tabla 'master_directive' con las configuraciones predeterminadas
     * sólo si aún no existen registros.
     */
    public void initializeMasterDirectives() {
        if (masterDirectiveRepo.count() == 0) {
            List<MasterDirective> masterDirectives = List.of(
                // Configuración para permitir consumo no planificado
                createMasterDirective(
                    "Permitir Consumo No Planificado",
                    "Permite dispensación de materiales sin orden de producción",
                    "Esta configuración relaja el control estándar de MRP permitiendo dispensar materiales sin una orden de producción asociada. Debe usarse solo temporalmente durante la fase de adopción del sistema.",
                    "false",
                    MasterDirective.GRUPO.FLEXIBILIDAD_CONTROL
                ),
                
                // Configuración para permitir backflush no planificado
                createMasterDirective(
                    "Permitir Backflush No Planificado",
                    "Permite registrar productos terminados sin orden de producción",
                    "Esta configuración relaja el control estándar de MRP permitiendo registrar productos terminados sin una orden de producción asociada. Debe usarse solo temporalmente durante la fase de adopción del sistema.",
                    "false",
                    MasterDirective.GRUPO.FLEXIBILIDAD_CONTROL
                )
            );
            
            masterDirectiveRepo.saveAll(masterDirectives);
            System.out.println(">> Tabla de MasterDirective inicializada con valores por defecto");
        }
    }
    
    /**
     * Método auxiliar para crear una instancia de MasterDirective
     */
    private MasterDirective createMasterDirective(String nombre, String resumen, String ayuda, String valor, MasterDirective.GRUPO grupo) {
        MasterDirective directive = new MasterDirective();
        directive.setNombre(nombre);
        directive.setResumen(resumen);
        directive.setAyuda(ayuda);
        directive.setValor(valor);
        directive.setGrupo(grupo);
        return directive;
    }
}