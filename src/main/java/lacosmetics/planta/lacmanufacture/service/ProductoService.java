package lacosmetics.planta.lacmanufacture.service;


import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.dto.InsumoWithStockDTO;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.inventarios.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


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

    private final MovimientoRepo movimientoRepo;

    // fetch all products para el producto picker component en frontend
    public Page<Producto> getAllProductos(int page, int size){
        return productoRepo.findAll(PageRequest.of(page, size));
    }

    /**
     * no longer to be used to save a materia prima
     * @param producto
     * @return
     */
    @Transactional
    public Producto saveProducto(Producto producto){
        if (producto instanceof SemiTerminado semiTerminado) {
            return productoRepo.save(semiTerminado);
        } else{
            return productoRepo.save(producto);
        }
    }


    /**
     * the most up to date method to save materias primas, since saving their ficha tecnica
     * is now implemented, so previous methods are deprecated for this purpose.
     * @param materiaPrima
     * @param file
     * @return
     */
    @Transactional
    public MateriaPrima saveMateriaPrimaV2(MateriaPrima materiaPrima, MultipartFile file) {
        try {
            // Define the folder where the ficha técnica PDFs will be stored.
            Path folderPath = Paths.get("data", "fichas_tecnicas_mp");
            Files.createDirectories(folderPath);  // Create the folder if it doesn't exist.

            // Generate a unique filename using a UUID and the original filename.
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = folderPath.resolve(newFilename);

            // Copy the file's content to the target folder.
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Set the file path (or URL) to the MateriaPrima entity.
            materiaPrima.setFichaTecnicaUrl(filePath.toString());

            // Persist the MateriaPrima entity.
            return materiaPrimaRepo.save(materiaPrima);
        } catch (Exception e) {
            throw new RuntimeException("Error saving MateriaPrima with ficha técnica: " + e.getMessage(), e);
        }
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

    public Page<Terminado> searchByName_T(String searchTerm, int page, int size) {
        String[] searchTerms = searchTerm.toLowerCase().split(" ");
        Specification<Terminado> spec = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[searchTerms.length];
            for (int i = 0; i < searchTerms.length; i++) {
                predicates[i] = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")), "%" + searchTerms[i] + "%"
                );
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

    public Page<ProductoStockDTO> searchTerminadoAndSemiTerminadoWithStock(String searchTerm, String tipoBusqueda, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Producto> spec = (root, query, criteriaBuilder) -> {
            Predicate tipoPredicate = root.type().in(Terminado.class, SemiTerminado.class);

            if ("NOMBRE".equalsIgnoreCase(tipoBusqueda)) {
                Predicate nombrePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
                return criteriaBuilder.and(tipoPredicate, nombrePredicate);
            } else if ("ID".equalsIgnoreCase(tipoBusqueda)) {
                try {
                    Integer id = Integer.parseInt(searchTerm);
                    Predicate idPredicate = criteriaBuilder.equal(root.get("productoId"), id);
                    return criteriaBuilder.and(tipoPredicate, idPredicate);
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

    public List<InsumoWithStockDTO> getInsumosWithStock(int productoId) {
        Optional<Producto> optionalProducto = productoRepo.findById(productoId);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Insumo> insumos = new ArrayList<>();

            if (producto instanceof Terminado terminado) {
                insumos = terminado.getInsumos();
            } else if (producto instanceof SemiTerminado semiTerminado) {
                insumos = semiTerminado.getInsumos();
            } else {
                throw new RuntimeException("Producto must be Terminado or SemiTerminado");
            }

            List<InsumoWithStockDTO> insumosWithStock = new ArrayList<>();
            for (Insumo insumo : insumos) {
                Producto insumoProducto = insumo.getProducto();
                Double stockActual = movimientoRepo.findTotalCantidadByProductoId(insumoProducto.getProductoId());
                stockActual = (stockActual != null) ? stockActual : 0.0;

                InsumoWithStockDTO dto = new InsumoWithStockDTO();
                dto.setInsumoId(insumo.getInsumoId());
                dto.setProductoId(insumoProducto.getProductoId());
                dto.setProductoNombre(insumoProducto.getNombre());
                dto.setCantidadRequerida(insumo.getCantidadRequerida());
                dto.setStockActual(stockActual);

                insumosWithStock.add(dto);
            }
            return insumosWithStock;
        } else {
            throw new RuntimeException("Producto not found");
        }
    }

}
