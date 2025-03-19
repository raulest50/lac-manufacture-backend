package lacosmetics.planta.lacmanufacture.service;


import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.compras.ItemOrdenCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
import lacosmetics.planta.lacmanufacture.model.dto.DocIngresoDTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.DocIngresoAlmacenOC;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.DocIngresoAlmacenOCRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.MovimientoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MateriaPrimaRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class MovimientosService {

    private final MovimientoRepo movimientoRepo;
    private final ProductoRepo productoRepo;

    private final DocIngresoAlmacenOCRepo docIngresoRepo;
    private final OrdenCompraRepo ordenCompraRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;
    private final MateriaPrimaRepo materiaPrimaRepo;

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


    /**
     * El registro de esta entidad en el sistema implica el ingreso de mercancia al almacen. por tanto
     * esto se debe ver reflejado inmediatamente en la tabla de movimientos, y actualizar los precios de cada
     * materia prima y de las recetas dependientes de cada materia prima. tambien el estado de la orden de compra
     * debe cambiar automaticamente a 3, que es cerrada exitosamente
     * @param docIngresoDTO
     * @param file
     * @return
     */
    @Transactional
    public ResponseEntity<?> createDocIngreso(DocIngresoDTA docIngresoDTO, MultipartFile file) {
        try {
            // Create folder based on current date (yyyyMMdd)
            String currentDateFolder = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path folderPath = Paths.get("data", currentDateFolder);
            Files.createDirectories(folderPath);

            // Generate a unique filename using a UUID and the original file name.
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = folderPath.resolve(newFilename);

            // Save the file to disk.
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create the DocIngresoAlmacenOC entity using the DTO constructor.
            DocIngresoAlmacenOC docIngreso = new DocIngresoAlmacenOC(docIngresoDTO);
            // Set the URL (or path) of the saved file.
            docIngreso.setUrlDocSoporte(filePath.toString());

            // Set the back-reference for each Movimiento so that their foreign key is updated.
            if (docIngreso.getItemsDocIngreso() != null) {
                docIngreso.getItemsDocIngreso().forEach(movimiento -> movimiento.setDocumentoMovimiento(docIngreso));
            }

            // Persist the entity.
            docIngresoRepo.save(docIngreso);

            // se actualiza el estado de la orden de compra a cerrado exitosamente
            OrdenCompra oc = docIngresoDTO.getOrdenCompra();
            oc.setEstado(3);
            ordenCompraRepo.save(oc);


            // se actualizan los precios de todos las materias primas
            for (ItemOrdenCompra itemOrdenCompra : oc.getItemsOrdenCompra()) {
                itemOrdenCompra.setOrdenCompra(oc);

                // Verify that the MateriaPrima exists
                Optional<Material> optionalMateriaPrima = materiaPrimaRepo.findById(itemOrdenCompra.getMaterial().getProductoId());
                if (!optionalMateriaPrima.isPresent()) {
                    throw new RuntimeException("MateriaPrima not found with ID: " + itemOrdenCompra.getMaterial().getProductoId());
                }
                Material material = optionalMateriaPrima.get();
                itemOrdenCompra.setMaterial(material);

                // Retrieve current stock
                Double currentStockOpt = movimientoRepo.findTotalCantidadByProductoId(material.getProductoId());
                int nuevoCosto = getNuevoCosto(itemOrdenCompra, currentStockOpt, material);

                // Update MateriaPrima's costo
                material.setCosto(nuevoCosto);

                // Save the updated MateriaPrima
                materiaPrimaRepo.save(material);

                // Update costs of dependent products if necessary
                Set<Integer> updatedProductIds = new HashSet<>();
                updateCostoCascade(material, updatedProductIds);
            }



            return ResponseEntity.ok(docIngreso);
        } catch(Exception e) {
            log.error("Error saving DocIngresoAlmacenOC", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving document: " + e.getMessage());
        }
    }


    private void updateCostoCascade(Producto producto, Set<Integer> updatedProductIds) {
        // If we've already updated this product, return to prevent infinite recursion
        if (updatedProductIds.contains(producto.getProductoId())) {
            return;
        }
        updatedProductIds.add(producto.getProductoId());

        // Recalculate cost of the product if it's a SemiTerminado or Terminado
        if (producto instanceof SemiTerminado) {
            SemiTerminado semiTerminado = (SemiTerminado) producto;

            // Recalculate cost
            double newCosto = 0;
            for (Insumo insumo : semiTerminado.getInsumos()) {
                Producto insumoProducto = insumo.getProducto();
                double insumoCosto = insumoProducto.getCosto();
                double cantidadRequerida = insumo.getCantidadRequerida();
                newCosto += insumoCosto * cantidadRequerida;
            }
            semiTerminado.setCosto((int) newCosto);

            // Save updated SemiTerminado
            semiTerminadoRepo.save(semiTerminado);

        } else if (producto instanceof Terminado) {
            Terminado terminado = (Terminado) producto;

            // Recalculate cost
            double newCosto = 0;
            for (Insumo insumo : terminado.getInsumos()) {
                Producto insumoProducto = insumo.getProducto();
                double insumoCosto = insumoProducto.getCosto();
                double cantidadRequerida = insumo.getCantidadRequerida();
                newCosto += insumoCosto * cantidadRequerida;
            }
            terminado.setCosto((int) newCosto);

            // Save updated Terminado
            terminadoRepo.save(terminado);
        }

        // Now find any SemiTerminados that use this product as an Insumo
        List<SemiTerminado> semiTerminados = semiTerminadoRepo.findByInsumos_Producto(producto);
        for (SemiTerminado st : semiTerminados) {
            updateCostoCascade(st, updatedProductIds);
        }

        // And find any Terminados that use this product as an Insumo
        List<Terminado> terminados = terminadoRepo.findByInsumos_Producto(producto);
        for (Terminado t : terminados) {
            updateCostoCascade(t, updatedProductIds);
        }
    }


    private static int getNuevoCosto(ItemOrdenCompra itemOrdenCompra, Double currentStockOpt, Material material) {
        double currentStock = (currentStockOpt != null) ? currentStockOpt : 0;

        // Retrieve current costo
        double currentCosto = material.getCosto();

        // Incoming units and precioCompra from ItemCompra
        double incomingUnits = itemOrdenCompra.getCantidad();
        double incomingPrecio = itemOrdenCompra.getPrecioUnitarioFinal();

        // Calculate nuevo_costo
        if (currentStock + incomingUnits == 0) {
            throw new RuntimeException("Total stock cannot be zero after the compra for MateriaPrima ID: " + material.getProductoId());
        }

        double nuevoCosto = ((currentCosto * currentStock) + (incomingPrecio * incomingUnits)) / (currentStock + incomingUnits);
        return  (int) Math.ceil(nuevoCosto);
    }

}
