package lacosmetics.planta.lacmanufacture.service;


import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.GrupoMovimeintoMP;
import lacosmetics.planta.lacmanufacture.model.notPersisted.ReporteCompraDTA;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.repo.GrupoMovimientoMpRepo;
import lacosmetics.planta.lacmanufacture.repo.MateriaPrimaRepo;
import lacosmetics.planta.lacmanufacture.resource.HyLIntegracionesResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class HyLIntegracionService {

    private final MateriaPrimaRepo materiaPrimaRepo;
    private final GrupoMovimientoMpRepo grupoMovimientoMpRepo;

    public MateriaPrima codificarMateriaPrima(MateriaPrima materiaPrima) {
        Optional<MateriaPrima> existente = materiaPrimaRepo.findById(materiaPrima.getReferencia());
        if (existente.isPresent()) {
            return materiaPrima; // si ya existe, en caso de que HyL envie 2 veces por error simplemente no se hace nada
        } else {
            return materiaPrimaRepo.save(materiaPrima); // si no existe se codifica
        }
    }


    @Transactional(rollbackOn = Exception.class)
    public GrupoMovimeintoMP reportarCompraHyL(ReporteCompraDTA reporteCompra) {

        Optional<GrupoMovimeintoMP> existenteGmMp = grupoMovimientoMpRepo.findById(reporteCompra.getIdcompra());

        if(existenteGmMp.isPresent()) throw new EntityExistsException("La factura con referencia " + reporteCompra.getIdcompra() + " ya fue registrada");
        else{
            List<ItemReporteCompraMP> reporteCompraMPList = new ArrayList<>();

            for(MateriaPrima m : reporteCompra.getProductos()){ // revisar que todos los mp en la factura existan en la bd
                Optional<MateriaPrima> existente = materiaPrimaRepo.findById(m.getReferencia());
                if(existente.isPresent()){
                    reporteCompraMPList.add(new ItemReporteCompraMP(existente.get(), m.getCantidad(), m.getCosto()));
                } else throw new EntityNotFoundException("La materia prima con referencia " + m.getReferencia() + " no está registrada en la base de datos.");
            }

            GrupoMovimeintoMP grupoMovimeintoMP = new GrupoMovimeintoMP(reporteCompra);

            grupoMovimientoMpRepo.save(grupoMovimeintoMP);

            for( ItemReporteCompraMP item : reporteCompraMPList){
                item.materiaPrima.setCantidad( item.materiaPrima.getCantidad() + item.cambioCantidad );
                item.materiaPrima.setCosto(item.ultimoCosto);
                materiaPrimaRepo.save(item.materiaPrima); // se hace update de la nueva cantidad y del ultimo costo
            }

            return grupoMovimeintoMP;
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    class ItemReporteCompraMP {
        public MateriaPrima materiaPrima;
        public double cambioCantidad;
        public int ultimoCosto;
    }
}
