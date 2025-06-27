package lacosmetics.planta.lacmanufacture.service;


import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.dto.InsumoWithStockDTO;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.dto.productos.search.ProductoSearchCriteria;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.*;
import lacosmetics.planta.lacmanufacture.service.commons.FileStorageService;
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

import java.util.*;
import java.util.stream.Collectors;




@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepo productoRepo;
    private final MaterialRepo materialRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;

    private final InsumoRepo insumoRepository;

    private final TransaccionAlmacenRepo transaccionAlmacenRepo;

    private final FileStorageService fileStorageService;

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
        // Prevent accidental overwrite: if a material with the same productoId already exists, throw an exception.
        if (materialRepo.existsById(material.getProductoId())) {
            throw new IllegalArgumentException("El codigo: " + material.getProductoId() +
                    " ya esta asignado a otro Material");
        }
        try {
            // Solo guardar la ficha técnica si se proporciona un archivo
            if (file != null && !file.isEmpty()) {
                String fichaTecnicaPath = fileStorageService.storeFichaTecnica(file);
                material.setFichaTecnicaUrl(fichaTecnicaPath);
            }
            return materialRepo.save(material); // Persist the MateriaPrima entity.
        } catch (Exception e) {
            throw new RuntimeException("Error saving MateriaPrima: " + e.getMessage(), e);
        }
    }

    public Page<Producto> searchP4RecetaV2(String searchTerm, String tipoBusqueda, String clasificacion, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if ("ID".equalsIgnoreCase(tipoBusqueda)) {
            String id = searchTerm;
            if ("M".equalsIgnoreCase(clasificacion)) { // return MateriaPrima list
                Optional<Material> mpOpt = materialRepo.findById(id);
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
            return Page.empty(pageable);
        } else { // Name search with partial matching
            if ("M".equalsIgnoreCase(clasificacion)) {
                Specification<Material> spec = (root, query, cb) ->
                        cb.like(cb.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
                Page<Material> result = materialRepo.findAll(spec, pageable);
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

        return materialRepo.findAll(spec, PageRequest.of(page, size));
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

    public Optional<Material> findMateriaPrimaByProductoId(String productoId) {
        return materialRepo.findById(productoId);
    }

    public Optional<SemiTerminado> findSemiTerminadoByProductoId(String productoId) {
        return semiTerminadoRepo.findById(productoId);
    }

    public Optional<Terminado> findTerminadoByProductoId(String productoId) {
        return terminadoRepo.findById(productoId);
    }

    public Page<ProductoStockDTO> searchTerminadoAndSemiTerminadoWithStock(String searchTerm, String tipoBusqueda, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Producto> spec = (root, query, criteriaBuilder) -> {
            Predicate tipoPredicate = root.type().in(Terminado.class, SemiTerminado.class);

            if ("NOMBRE".equalsIgnoreCase(tipoBusqueda)) {
                Predicate nombrePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
                return criteriaBuilder.and(tipoPredicate, nombrePredicate);
            } else if ("ID".equalsIgnoreCase(tipoBusqueda)) {
                String id = searchTerm;
                Predicate idPredicate = criteriaBuilder.equal(root.get("productoId"), id);
                return criteriaBuilder.and(tipoPredicate, idPredicate);
            } else {
                return null;
            }
        };

        Page<Producto> productosPage = productoRepo.findAll(spec, pageable);

        List<ProductoStockDTO> productStockDTOList = productosPage.getContent().stream().map(producto -> {
            Double stockQuantity = transaccionAlmacenRepo.findTotalCantidadByProductoId(producto.getProductoId());
            stockQuantity = (stockQuantity != null) ? stockQuantity : 0.0;
            return new ProductoStockDTO(producto, stockQuantity);
        }).collect(Collectors.toList());

        return new PageImpl<>(productStockDTOList, pageable, productosPage.getTotalElements());
    }

    public List<InsumoWithStockDTO> getInsumosWithStock(String productoId) {
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
                Double stockActual = transaccionAlmacenRepo.findTotalCantidadByProductoId(insumoProducto.getProductoId());
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
                    String productoId;
                    // Read the product ID (you might add more validation if needed)
                    try {
                        if (cellProductoId.getCellType() == CellType.NUMERIC) {
                            // Si es un valor numérico, convertirlo a String sin forzar a entero
                            productoId = String.valueOf(cellProductoId.getNumericCellValue()).replaceAll("\\.0$", "");
                        } else {
                            // Si es un valor de texto, obtenerlo directamente
                            productoId = cellProductoId.getStringCellValue().trim();
                        }
                    } catch (Exception e) {
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

                    materialRepo.save(mp);
                    insertedCount++;
                }
            }
        }
        return insertedCount;
    }


    public Page<Producto> consultaProductos(String search,
                                            List<String> categories,
                                            int page,
                                            int size) {

        Pageable pageable = PageRequest.of(page, size);

        // 1) Sin categorías → vacío
        if (categories == null || categories.isEmpty()) {
            return Page.empty(pageable);
        }


        Specification<Producto> spec = (root, query, cb) -> {
            boolean mp = categories.contains(ProductoSearchCriteria.CATEGORIA_MATERIA_PRIMA);
            boolean me = categories.contains(ProductoSearchCriteria.CATEGORIA_MATERIAL_EMPAQUE);
            boolean se = categories.contains(ProductoSearchCriteria.CATEGORIA_SEMITERMINADO);
            boolean te = categories.contains(ProductoSearchCriteria.CATEGORIA_TERMINADO);

            List<Predicate> preds = new ArrayList<>();

            // Filtrar Material según tipoMaterial
            if (mp) {
                preds.add(cb.and(
                        cb.equal(root.type(), Material.class),
                        cb.equal(cb.treat(root, Material.class).get("tipoMaterial"), 1)
                ));
            }
            if (me) {
                preds.add(cb.and(
                        cb.equal(root.type(), Material.class),
                        cb.equal(cb.treat(root, Material.class).get("tipoMaterial"), 2)
                ));
            }

            // Filtrar Semiterminado
            if (se) {
                preds.add(cb.equal(root.type(), SemiTerminado.class));
            }
            // Filtrar Terminado
            if (te) {
                preds.add(cb.equal(root.type(), Terminado.class));
            }

            // OR de todas las categorías seleccionadas
            Predicate categoryPredicate = cb.or(preds.toArray(new Predicate[0]));

            // Si hay término de búsqueda, combinamos con AND
            if (search != null && !search.trim().isEmpty()) {
                String[] terms = search.trim().toLowerCase().split("\\s+");
                List<Predicate> searchPreds = new ArrayList<>();
                for (String term : terms) {
                    searchPreds.add(cb.like(
                            cb.lower(root.get("nombre")),
                            "%" + term + "%"
                    ));
                }
                Predicate searchPredicate = cb.and(searchPreds.toArray(new Predicate[0]));
                return cb.and(categoryPredicate, searchPredicate);
            }

            // Sólo categorías
            return categoryPredicate;
        };

        return productoRepo.findAll(spec, pageable);
    }




}
