package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Producto;
import lacosmetics.planta.lacmanufacture.repo.ProductoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepo productoRepo;

    public Page<Producto> getAllProductos(int page, int size){
        return productoRepo.findAll(PageRequest.of(page, size));
    }

    public Producto getProductoById(int id){
        return productoRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Producto no encontrado"));
    }

    public Producto saveProducto(Producto producto){
        return productoRepo.save(producto);
    }

    public void deleteProducto(int id) {
        productoRepo.deleteById(id);
    }
}
