package lacosmetics.planta.lacmanufacture.service;


import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
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
    private final MateriaPrimaRepo materiaPrimaRepo;

    @Autowired
    private final SemiTerminadoRepo semiTerminadoRepo;

    @Autowired
    private final TerminadoRepo terminadoRepo;

    @Autowired
    private final InsumoRepo insumoRepository;


    // se hace un metodo por aparte para hacer modificaciones pero que HyL no tenga acceso a el
    // para evitar que sobre escriba datos al realizar codificacion 2 veces
    public MateriaPrima updateMateriaPrima(MateriaPrima materiaPrima) {
        return materiaPrimaRepo.save(materiaPrima);
    }


    public Page<MateriaPrima> getAllMP(int page, int size) {
        return materiaPrimaRepo.findAll(PageRequest.of(page, size));
    }

    public Page<SemiTerminado> getAllS(int page, int size) {
        return semiTerminadoRepo.findAll(PageRequest.of(page, size));
    }


    // para obtener todos los productos clase Termindo
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

    public Page<MateriaPrima> getPendientesFromHyL(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return materiaPrimaRepo.findByContenidoPorUnidadIsOrTipoUnidadesIsNull(0.0, pageRequest);
    }
}
