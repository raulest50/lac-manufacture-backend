package lacosmetics.planta.lacmanufacture.service.inventarios;


import org.springframework.transaction.annotation.Transactional;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.compras.ItemOrdenCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.dto.compra.materiales.IngresoOCM_DTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.Lote;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.LoteRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.service.contabilidad.ContabilidadService;
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

import java.math.BigDecimal;
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
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MovimientosService {

    private final TransaccionAlmacenRepo transaccionAlmacenRepo;
    private final ProductoRepo productoRepo;
    private final TransaccionAlmacenHeaderRepo transaccionAlmacenHeaderRepo;

    private final OrdenCompraRepo ordenCompraRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;
    private final MaterialRepo materialRepo;
    private final LoteRepo loteRepo;
    private final UserRepository userRepository;
    private final ContabilidadService contabilidadService;

    @Transactional
    public Movimiento saveMovimiento(Movimiento movimientoReal){
        return transaccionAlmacenRepo.save(movimientoReal);
    }

    public Optional<ProductoStockDTO> getStockOf(String producto_id){
        Optional<Producto> optionalProducto = productoRepo.findByProductoId(producto_id);
        if(optionalProducto.isPresent()){
            Double totalCantidad = transaccionAlmacenRepo.findTotalCantidadByProductoId(producto_id);
            totalCantidad = (totalCantidad != null) ? totalCantidad : 0.0;
            ProductoStockDTO productoStock = new ProductoStockDTO(optionalProducto.get(), totalCantidad);
            return Optional.of(productoStock);
        } else{
            return Optional.of(new ProductoStockDTO());
        }
    }

    public Optional<ProductoStockDTO> getStockOf2(String producto_id){
        Optional<Producto> optionalProducto = productoRepo.findByProductoId(producto_id);
        if(optionalProducto.isEmpty()){
            return Optional.empty();
        }

        List<Movimiento> movimientos = transaccionAlmacenRepo.findByProducto_ProductoId(producto_id);
        double productoStock = movimientos.stream()
                .mapToDouble(Movimiento::getCantidad)
                .sum();

        return Optional.of(new ProductoStockDTO(optionalProducto.get(), productoStock));
    }


    // New method to search products and get stock
    public Page<ProductoStockDTO> searchProductsWithStock(String searchTerm, String tipoBusqueda, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Producto> spec = (root, query, criteriaBuilder) -> {
            if ("NOMBRE".equalsIgnoreCase(tipoBusqueda)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + searchTerm.toLowerCase() + "%");
            } else if ("ID".equalsIgnoreCase(tipoBusqueda)) {
                // Usar directamente el searchTerm como String para la comparación
                return criteriaBuilder.equal(root.get("productoId"), searchTerm);
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


    // Method to get movimientos for a product
    public Page<Movimiento> getMovimientosByProductoId(String productoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transaccionAlmacenRepo.findByProducto_ProductoIdOrderByFechaMovimientoDesc(productoId, pageable);
    }


    /**
     * El registro de esta entidad en el sistema implica el ingreso de mercancia al almacen. por tanto
     * esto se debe ver reflejado inmediatamente en la tabla de movimientos, y actualizar los precios de cada
     * materia prima y de las recetas dependientes de cada materia prima. tambien el estado de la orden de compra
     * debe cambiar automaticamente a 3, que es cerrada exitosamente
     * @param ingresoOCM_dta
     * @param file
     * @return
     */
    @Transactional
    public ResponseEntity<?> createDocIngreso(IngresoOCM_DTA ingresoOCM_dta, MultipartFile file) {
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
            TransaccionAlmacen ingresoOCM = new TransaccionAlmacen(ingresoOCM_dta);
            // Set the URL (or path) of the saved file.
            ingresoOCM.setUrlDocSoporte(filePath.toString());

            // Set the user if userId is provided
            if (ingresoOCM_dta.getUserId() != null && !ingresoOCM_dta.getUserId().isEmpty()) {
                try {
                    Long userId = Long.parseLong(ingresoOCM_dta.getUserId());
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
                    ingresoOCM.setUser(user);
                } catch (NumberFormatException e) {
                    log.error("Error al convertir userId a Long: " + ingresoOCM_dta.getUserId(), e);
                }
            }

            // Set the back-reference for each Movimiento and create Lote for each one
            if (ingresoOCM.getMovimientosTransaccion() != null) {
                for (Movimiento movimiento : ingresoOCM.getMovimientosTransaccion()) {
                    movimiento.setTransaccionAlmacen(ingresoOCM);

                    // Crear un nuevo lote para este movimiento
                    Lote lote = new Lote();
                    lote.setBatchNumber(generateBatchNumber(movimiento.getProducto()));

                    // Verificar si el movimiento ya tiene un lote con fecha de fabricación especificada
                    if (movimiento.getLote() != null && movimiento.getLote().getProductionDate() != null) {
                        // Si se proporcionó una fecha de fabricación, usarla
                        lote.setProductionDate(movimiento.getLote().getProductionDate());
                    }
                    // Si no se proporcionó fecha de fabricación, se deja como null

                    // Verificar si el movimiento ya tiene un lote con fecha de vencimiento especificada
                    if (movimiento.getLote() != null && movimiento.getLote().getExpirationDate() != null) {
                        // Si se proporcionó una fecha de vencimiento, usarla
                        lote.setExpirationDate(movimiento.getLote().getExpirationDate());
                    }
                    // Si no se proporcionó fecha de vencimiento, se deja como null

                    // Asociar con la orden de compra
                    lote.setOrdenCompraMateriales(ingresoOCM_dta.getOrdenCompraMateriales());

                    // Guardar el lote
                    loteRepo.save(lote);

                    // Asociar el lote al movimiento
                    movimiento.setLote(lote);
                }
            }

            // Persist the entity.
            transaccionAlmacenHeaderRepo.save(ingresoOCM);

            // se actualiza el estado de la orden de compra a cerrado exitosamente
            OrdenCompraMateriales oc = ingresoOCM_dta.getOrdenCompraMateriales();
            oc.setEstado(3);
            ordenCompraRepo.save(oc);

            // Calcular el monto total para el asiento contable
            BigDecimal montoTotal = BigDecimal.ZERO;
            for (ItemOrdenCompra itemOrdenCompra : oc.getItemsOrdenCompra()) {
                BigDecimal valorItem = BigDecimal.valueOf(itemOrdenCompra.getPrecioUnitario() * itemOrdenCompra.getCantidad());
                montoTotal = montoTotal.add(valorItem);
            }

            // Registrar el asiento contable
            try {
                AsientoContable asiento = contabilidadService.registrarAsientoIngresoOCM(ingresoOCM, oc, montoTotal);
                log.info("Asiento contable registrado con ID: " + asiento.getId());
            } catch (Exception e) {
                log.error("Error al registrar asiento contable: " + e.getMessage(), e);
                // No interrumpimos el flujo principal si falla la contabilidad
            }

            // se actualizan los precios de todos las materias primas
            for (ItemOrdenCompra itemOrdenCompra : oc.getItemsOrdenCompra()) {
                itemOrdenCompra.setOrdenCompraMateriales(oc);

                // Verify that the MateriaPrima exists
                Optional<Material> optionalMateriaPrima = materialRepo.findById(itemOrdenCompra.getMaterial().getProductoId());
                if (!optionalMateriaPrima.isPresent()) {
                    throw new RuntimeException("MateriaPrima not found with ID: " + itemOrdenCompra.getMaterial().getProductoId());
                }
                Material material = optionalMateriaPrima.get();
                itemOrdenCompra.setMaterial(material);

                // Retrieve current stock
                Double currentStockOpt = transaccionAlmacenRepo.findTotalCantidadByProductoId(material.getProductoId());
                int nuevoCosto = getNuevoCosto(itemOrdenCompra, currentStockOpt, material);

                // Update MateriaPrima's costo
                material.setCosto(nuevoCosto);

                // Save the updated MateriaPrima
                materialRepo.save(material);

                // Update costs of dependent products if necessary
                Set<String> updatedProductIds = new HashSet<>();
                updateCostoCascade(material, updatedProductIds);
            }

            return ResponseEntity.ok(ingresoOCM);
        } catch(Exception e) {
            log.error("Error saving DocIngresoAlmacenOC", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving document: " + e.getMessage());
        }
    }

    /**
     * Genera un número de lote único para un producto
     */
    private String generateBatchNumber(Producto producto) {
        // Formato: MP-YYYYMMDD-XXXX (MP=Materia Prima, fecha, secuencial)
        String prefix = "MP";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return prefix + "-" + date + "-" + random;
    }


    private void updateCostoCascade(Producto producto, Set<String> updatedProductIds) {
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
