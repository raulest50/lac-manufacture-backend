package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class MovimientosService {

//    private final MovimientoRepo movimientoRepo;

//    @Transactional
//    public Movimiento saveMovimiento(Movimiento movimiento){
//        return movimientoRepo.save(movimiento);
//    }

/*    public Optional<Stock> getStockOf(int producto_id){
        Optional<ProductoExotic> optionalProducto = productoRepo.findByProductoId(producto_id);
        if(optionalProducto.isPresent()){
            Double totalCantidad = movimientoRepo.findTotalCantidadByProductoId(producto_id);
            totalCantidad = (totalCantidad != null) ? totalCantidad : 0.0;
            Stock stock = new Stock(totalCantidad, optionalProducto.get());
            return Optional.of(stock);
        } else{
            return Optional.of(new Stock());
        }
    }*/

/*    public Optional<Stock> getStockOf2(int producto_id){
        List<Movimiento> movs = movimientoRepo.findMovimientosByCantidad( Double.valueOf( (double) producto_id) );
        if(!movs.isEmpty()){
            double stock = movs.stream().mapToDouble(Movimiento::getCantidad).sum();
            return Optional.of(new Stock(stock, movs.getFirst().getProducto()));
        } else{
            return Optional.empty();
        }
    }*/

}
