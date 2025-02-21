package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.repo.inventarios.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<ProductoStockDTO> getStockOf(int producto_id){
        Optional<Producto> optionalProducto = productoRepo.findByProductoId(producto_id);
        if(optionalProducto.isPresent()){
            Double totalCantidad = movimientoRepo.findTotalCantidadByProductoId(producto_id);
            totalCantidad = (totalCantidad != null) ? totalCantidad : 0.0;
            ProductoStockDTO productoStock = new ProductoStockDTO(optionalProducto.get(), totalCantidad);
            return Optional.of(productoStock);
        } else{
            return Optional.of(new ProductoStockDTO());
        }
    }

    public Optional<ProductoStockDTO> getStockOf2(int producto_id){
        List<Movimiento> movs = movimientoRepo.findMovimientosByCantidad( Double.valueOf( (double) producto_id) );
        if(!movs.isEmpty()){
            double productoStock = movs.stream().mapToDouble(Movimiento::getCantidad).sum();
            return Optional.of(new ProductoStockDTO(movs.getFirst().getProducto(), productoStock));
        } else{
            return Optional.empty();
        }
    }


    // New method to search products and get stock
    public Page<ProductoStockDTO> searchProductsWithStock(String searchTerm, String tipoBusqueda, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Producto> spec = (root, query, criteriaBuilder) -> {
            if ("NOMBRE".equalsIgnoreCase(tipoBusqueda)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
            } else if ("ID".equalsIgnoreCase(tipoBusqueda)) {
                try {
                    Integer id = Integer.parseInt(searchTerm);
                    return criteriaBuilder.equal(root.get("productoId"), id);
                } catch (NumberFormatException e) {
                    return criteriaBuilder.disjunction(); // Return no results if ID is invalid
                }
            } else {
                return null;
            }
        };

        Page<Producto> productosPage = productoRepo.findAll(spec, pageable);

        List<ProductoStockDTO> productStockDTOList = productosPage.getContent().stream().map(producto -> {
            Double stockQuantity = movimientoRepo.findTotalCantidadByProductoId(producto.getProductoId());
            stockQuantity = (stockQuantity != null) ? stockQuantity : 0.0;
            return new ProductoStockDTO(producto, stockQuantity);
        }).collect(Collectors.toList());

        return new PageImpl<>(productStockDTOList, pageable, productosPage.getTotalElements());
    }


    // Method to get movimientos for a product
    public Page<Movimiento> getMovimientosByProductoId(int productoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movimientoRepo.findByProducto_ProductoIdOrderByFechaMovimientoDesc(productoId, pageable);
    }

}
