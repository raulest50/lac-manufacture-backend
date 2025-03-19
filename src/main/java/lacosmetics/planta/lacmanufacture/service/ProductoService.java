package lacosmetics.planta.lacmanufacture.service;


import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.dto.InsumoWithStockDTO;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.inventarios.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
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
import java.util.*;
import java.util.stream.Collectors;




@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepo productoRepo;
    private final MateriaPrimaRepo materiaPrimaRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;

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
     * @param material
     * @param file
     * @return
     */
    @Transactional
    public Material saveMateriaPrimaV2(Material material, MultipartFile file) {
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
            material.setFichaTecnicaUrl(filePath.toString());

            // Persist the MateriaPrima entity.
            return materiaPrimaRepo.save(material);
        } catch (Exception e) {
            throw new RuntimeException("Error saving MateriaPrima with ficha técnica: " + e.getMessage(), e);
        }
    }

    public Page<Producto> searchP4RecetaV2(String searchTerm, String tipoBusqueda, String clasificacion, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if ("ID".equalsIgnoreCase(tipoBusqueda)) {
            try {
                int id = Integer.parseInt(searchTerm);
                if ("M".equalsIgnoreCase(clasificacion)) { // return MateriaPrima list
                    Optional<Material> mpOpt = materiaPrimaRepo.findById(id);
                    List<Material> result = mpOpt.map(List::of).orElse(List.of());
                    List<Producto> productos = new ArrayList<>();
                    productos.addAll(result);
                    return new PageImpl<>(productos, pageable, productos.size());
                } else if ("S".equalsIgnoreCase(clasificacion)) {
                    Optional<SemiTerminado> stOpt = semiTerminadoRepo.findById(id);
                    List<SemiTerminado> result = stOpt.map(List::of).orElse(List.of());
                    List<Producto> productos = new ArrayList<>();
                    productos.addAll(result);
                    return new PageImpl<>(productos, pageable, productos.size());
                }
            } catch (NumberFormatException e) {
                return Page.empty(pageable);
            }
        } else { // Name search with partial matching
            if ("M".equalsIgnoreCase(clasificacion)) {
                Specification<Material> spec = (root, query, cb) ->
                        cb.like(cb.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
                Page<Material> result = materiaPrimaRepo.findAll(spec, pageable);
                List<Producto> productos = new ArrayList<>();
                productos.addAll(result.getContent());
                return new PageImpl<>(productos, pageable, result.getTotalElements());
            } else if ("S".equalsIgnoreCase(clasificacion)) {
                Specification<SemiTerminado> spec = (root, query, cb) ->
                        cb.like(cb.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
                Page<SemiTerminado> result = semiTerminadoRepo.findAll(spec, pageable);
                List<Producto> productos = new ArrayList<>();
                productos.addAll(result.getContent());
                return new PageImpl<>(productos, pageable, result.getTotalElements());
            }
        }
        return Page.empty(pageable);
    }


    // para obtener todos los productos clase Termindo
    public Page<Terminado> getAllT(int page, int size) {
        return terminadoRepo.findAll(PageRequest.of(page, size));
    }

    public Page<Material> searchByName_MP(String searchTerm, int page, int size){
        String[] searchTerms = searchTerm.toLowerCase().split(" ");
        Specification<Material> spec = (root, query, criteriaBuilder) -> {
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

    public Optional<Material> findMateriaPrimaByProductoId(int productoId) {
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


    /**
     * para hacer carga masiva desde archivo de excel
     * @param file
     * @return
     * @throws Exception
     */
    @Transactional
    public int bulkUploadMateriasPrimas(MultipartFile file) throws Exception {
        int insertedCount = 0;
        // List of sheet names of interest
        List<String> sheetNames = Arrays.asList("MATERIA PRIMA", "FRAGANCIA", "ETIQUETAS");

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            for (String sheetName : sheetNames) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    continue; // If the sheet does not exist, skip it
                }
                // Determine tipoMateriaPrima: 1 for "MATERIA PRIMA" and "FRAGANCIA", 2 for "ETIQUETAS"
                int tipoMateria = (sheetName.equalsIgnoreCase("MATERIA PRIMA") || sheetName.equalsIgnoreCase("FRAGANCIA")) ? 1 : 2;

                // Iterate over rows, starting at row 1 (skipping header)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Cell cellName = row.getCell(0);
                    Cell cellTipoUnidad = row.getCell(1);
                    Cell cellProductoId = row.getCell(2);

                    if (cellName == null || cellTipoUnidad == null || cellProductoId == null) continue;

                    // Read cell values (trim spaces)
                    String nombre = cellName.getStringCellValue().trim();
                    String tipoUnidades = cellTipoUnidad.getStringCellValue().trim();
                    int productoId;
                    // Read the product ID as numeric (you might add more validation if needed)
                    try {
                        productoId = (int) cellProductoId.getNumericCellValue();
                    } catch (Exception ex) {
                        continue; // Skip rows with invalid product id
                    }

                    // Check if product already exists (skip if exists)
                    if (productoRepo.existsById(productoId)) {
                        continue;
                    }

                    // Create and populate a new MateriaPrima instance
                    Material mp = new Material();
                    mp.setProductoId(productoId);
                    mp.setNombre(nombre);
                    mp.setTipoUnidades(tipoUnidades);
                    mp.setCosto(0);
                    mp.setObservaciones("");
                    mp.setCantidadUnidad(1);
                    mp.setTipoMaterial(tipoMateria);
                    // fechaCreacion is automatically set by @CreationTimestamp

                    materiaPrimaRepo.save(mp);
                    insertedCount++;
                }
            }
        }
        return insertedCount;
    }




    public Page<Producto> consultaProductos(String search, List<String> categories, int page, int size) {
        Specification<Producto> spec = Specification.where(null);

        // Use partial matching on 'nombre'
        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombre")), "%" + search.toLowerCase() + "%")
            );
        }

        // Map checkbox selections to product type and subcategory conditions
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Predicate categoryPredicate = cb.disjunction();
                if (categories.contains("materia prima")) {
                    // For MateriaPrima with tipoMateriaPrima = 1
                    categoryPredicate = cb.or(categoryPredicate,
                            cb.and(
                                    cb.equal(root.type(), Material.class),
                                    cb.equal(cb.treat(root, Material.class).get("tipoMateriaPrima"), 1)
                            )
                    );
                }
                if (categories.contains("material empaque")) {
                    // For MateriaPrima with tipoMateriaPrima = 2
                    categoryPredicate = cb.or(categoryPredicate,
                            cb.and(
                                    cb.equal(root.type(), Material.class),
                                    cb.equal(cb.treat(root, Material.class).get("tipoMateriaPrima"), 2)
                            )
                    );
                }
                if (categories.contains("semiterminado")) {
                    categoryPredicate = cb.or(categoryPredicate,
                            cb.equal(root.type(), SemiTerminado.class)
                    );
                }
                if (categories.contains("terminado")) {
                    categoryPredicate = cb.or(categoryPredicate,
                            cb.equal(root.type(), Terminado.class)
                    );
                }
                return categoryPredicate;
            });
        }

        return productoRepo.findAll(spec, PageRequest.of(page, size));
    }


}
