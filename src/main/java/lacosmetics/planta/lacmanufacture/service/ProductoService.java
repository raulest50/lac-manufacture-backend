package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.Insumo;
import lacosmetics.planta.lacmanufacture.model.Producto;
import lacosmetics.planta.lacmanufacture.model.SemiTerminado;
import lacosmetics.planta.lacmanufacture.repo.InsumoRepo;
import lacosmetics.planta.lacmanufacture.repo.ProductoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProductoService {

    @Autowired
    private final ProductoRepo productoRepo;

    @Autowired
    private final InsumoRepo insumoRepository;

    public Page<Producto> getAllProductos(int page, int size){
        return productoRepo.findAll(PageRequest.of(page, size));
    }

    public Page<Producto> getAllProductos_byType(int page, int size, String[] tipo_producto) {
        return productoRepo.findAllByTipoProductoIn(PageRequest.of(page, size), tipo_producto);
    }

    public Producto getProductoById(int id){
        return productoRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Producto no encontrado"));
    }

    @Transactional
    public Producto saveProducto(Producto producto){
        if (producto instanceof SemiTerminado semiTerminado) {
            return productoRepo.save(semiTerminado);
        } else{
            return productoRepo.save(producto);
        }
    }

    public void deleteProducto(int id) {
        productoRepo.deleteById(id);
    }

}
