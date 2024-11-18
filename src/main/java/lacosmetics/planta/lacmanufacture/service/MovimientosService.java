package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Movimiento;
import lacosmetics.planta.lacmanufacture.model.notPersisted.Stock;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.repo.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Optional<Producto> optionalProducto = productoRepo.findByProductoId(producto_id);
        if(optionalProducto.isPresent()){
            Double totalCantidad = movimientoRepo.findTotalCantidadByProductoId(producto_id);
            totalCantidad = (totalCantidad != null) ? totalCantidad : 0.0;
            Stock stock = new Stock(totalCantidad, optionalProducto.get());
            return Optional.of(stock);
        } else{
            return Optional.of(new Stock());
        }
    }

    public Optional<Stock> getStockOf2(int producto_id){
        List<Movimiento> movs = movimientoRepo.findMovimientosByCantidad( Double.valueOf( (double) producto_id) );
        if(!movs.isEmpty()){
            double stock = movs.stream().mapToDouble(Movimiento::getCantidad).sum();
            return Optional.of(new Stock(stock, movs.getFirst().getProducto()));
        } else{
            return Optional.empty();
        }
    }

}
