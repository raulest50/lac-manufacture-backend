package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.Producto;
import lacosmetics.planta.lacmanufacture.model.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.Terminado;
import lacosmetics.planta.lacmanufacture.repo.*;
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
    private final MateriaPrimaRepo materiaPrimaRepo;

    @Autowired
    private final SemiTerminadoRepo semiTerminadoRepo;

    @Autowired
    private final TerminadoRepo terminadoRepo;

    @Autowired
    private final InsumoRepo insumoRepository;


    public Page<Producto> getAllProductos(int page, int size){
        return productoRepo.findAll(PageRequest.of(page, size));
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

    public Page<MateriaPrima> getAllMP(int page, int size) {
        return materiaPrimaRepo.findAll(PageRequest.of(page, size));
    }

    public Page<SemiTerminado> getAllS(int page, int size) {
        return semiTerminadoRepo.findAll(PageRequest.of(page, size));
    }

    public Page<Terminado> getAllT(int page, int size) {
        return terminadoRepo.findAll(PageRequest.of(page, size));
    }
}
