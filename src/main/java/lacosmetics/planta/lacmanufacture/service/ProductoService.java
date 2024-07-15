package lacosmetics.planta.lacmanufacture.service;


import jakarta.persistence.criteria.Predicate;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

    public Page<MateriaPrima> searchByName_MP(String searchTerm, int page, int size){
        String[] searchTerms = searchTerm.toLowerCase().split(" ");
        Specification<MateriaPrima> spec = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[searchTerms.length];

            for (int i = 0; i < searchTerms.length; i++) {
                predicates[i] = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerms[i] + "%");
            }

            return criteriaBuilder.and(predicates);
        };

        return materiaPrimaRepo.findAll(spec, PageRequest.of(page, size));
    }

    public Page<SemiTerminado> searchByName_S(String searchTerm, int page, int size){
        String[] searchTerms = searchTerm.toLowerCase().split(" ");

        Specification<SemiTerminado> spec = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[searchTerms.length];

            for (int i = 0; i < searchTerms.length; i++) {
                predicates[i] = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerms[i] + "%");
            }

            return criteriaBuilder.and(predicates);
        };
        return semiTerminadoRepo.findAll(spec, PageRequest.of(page, size));
    }

    public Page<Terminado> searchByName_T(String searchTerm, int page, int size){
        String[] searchTerms = searchTerm.toLowerCase().split(" ");

        Specification<Terminado> spec = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[searchTerms.length];

            for (int i = 0; i < searchTerms.length; i++) {
                predicates[i] = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerms[i] + "%");
            }

            return criteriaBuilder.and(predicates);
        };
        return terminadoRepo.findAll(spec, PageRequest.of(page, size));
    }

    public Optional<MateriaPrima> findMateriaPrimaByProductoId(int productoId) {
        return materiaPrimaRepo.findById(productoId);
    }
    
    public Optional<SemiTerminado> findSemiTerminadoByProductoId(int productoId) {
        return semiTerminadoRepo.findById(productoId);
    }

    public Optional<Terminado> findTerminadoByProductoId(int productoId) {
        return terminadoRepo.findById(productoId);
    }
    
}
