package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Movimiento;
import lacosmetics.planta.lacmanufacture.model.notPersisted.Stock;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.repo.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.ProductoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class MovimientosService {

    private final MovimientoRepo movimientoRepo;
    private final ProductoRepo productoRepo;

    @Transactional
    public Movimiento saveMovimiento(Movimiento movimiento){
        return movimientoRepo.save(movimiento);
    }

    public Optional<Stock> getStockOf(int producto_id){
        Optional<Producto> optionalProducto = productoRepo.findById(producto_id);
        if(optionalProducto.isPresent()){
            Double totalCantidad = movimientoRepo.findTotalCantidadByProductoId(producto_id);
            Stock stock = new Stock(totalCantidad, optionalProducto.get());
            return Optional.of(stock);
        } else{
            return Optional.empty();
        }
    }

}
