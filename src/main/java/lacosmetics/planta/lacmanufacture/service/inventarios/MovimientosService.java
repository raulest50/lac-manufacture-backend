package lacosmetics.planta.lacmanufacture.service.inventarios;


import org.springframework.transaction.annotation.Transactional;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.compras.ItemOrdenCompra;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.AjusteInventarioDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.AjusteItemDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.BackflushNoPlanificadoDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.BackflushNoPlanificadoItemDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.BackflushMultipleNoPlanificadoDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionItemDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionNoPlanificadaDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.DispensacionNoPlanificadaItemDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.IngresoOCM_DTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.LoteDisponibleResponseDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.RecomendacionLotesRequestDTO;
import lacosmetics.planta.lacmanufacture.model.inventarios.Lote;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimiento;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.LoteRecomendadoDTO;
import lacosmetics.planta.lacmanufacture.model.producto.dto.ProductoStockDTO;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenProduccion;
import lacosmetics.planta.lacmanufacture.model.produccion.OrdenSeguimiento;
import lacosmetics.planta.lacmanufacture.model.produccion.dto.DispensacionFormularioDTO;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenSeguimientoRepo;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.produccion.OrdenProduccionRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.LoteRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenHeaderRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.ProductoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lacosmetics.planta.lacmanufacture.repo.master.configs.MasterDirectiveRepo;
import lacosmetics.planta.lacmanufacture.model.master.configs.MasterDirective;
import lacosmetics.planta.lacmanufacture.service.contabilidad.ContabilidadService;
import lacosmetics.planta.lacmanufacture.service.produccion.ProduccionService;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lacosmetics.planta.lacmanufacture.model.inventarios.dto.MovimientoExcelRequestDTO;

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
    private final ProduccionService produccionService;
    private final OrdenProduccionRepo ordenProduccionRepo;
    private final OrdenSeguimientoRepo ordenSeguimientoRepo;
    private final MasterDirectiveRepo masterDirectiveRepo;

    /**
     * Obtiene el formulario sugerido de dispensación delegando al servicio de producción.
     *
     * @param ordenProduccionId identificador de la orden de producción
     * @return formulario con materiales y lotes recomendados
     */
    public DispensacionFormularioDTO getFormularioDispensacion(int ordenProduccionId) {
        return produccionService.getFormularioDispensacion(ordenProduccionId);
    }

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


    public TransaccionAlmacen createAjusteInventario(AjusteInventarioDTO ajusteInventarioDTO) {
        TransaccionAlmacen transaccion = new TransaccionAlmacen();
        transaccion.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OAA);
        transaccion.setIdEntidadCausante(0);
        transaccion.setObservaciones(ajusteInventarioDTO.getObservaciones());
        transaccion.setUrlDocSoporte(ajusteInventarioDTO.getUrlDocSoporte());

        if (ajusteInventarioDTO.getUsername() != null && !ajusteInventarioDTO.getUsername().isEmpty()) {
            Optional<User> userOpt = userRepository.findByUsername(ajusteInventarioDTO.getUsername());
            if (userOpt.isPresent()) {
                transaccion.setUser(userOpt.get());
            } else {
                // Si no se encuentra el usuario, registramos un warning pero continuamos
                log.warn("Usuario no encontrado con username: " + ajusteInventarioDTO.getUsername());
            }
        }

        List<Movimiento> movimientos = new ArrayList<>();
        if (ajusteInventarioDTO.getItems() != null) {
            for (AjusteItemDTO item : ajusteInventarioDTO.getItems()) {
                Producto producto = productoRepo.findByProductoId(item.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getProductoId()));

                Movimiento movimiento = new Movimiento();
                movimiento.setCantidad(item.getCantidad());
                movimiento.setProducto(producto);
                movimiento.setAlmacen(Optional.ofNullable(item.getAlmacen()).orElse(Movimiento.Almacen.GENERAL));
                movimiento.setTipoMovimiento(resolveTipoMovimiento(item));

                if (item.getLoteId() != null) {
                    Lote lote = loteRepo.findById(item.getLoteId().longValue())
                            .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + item.getLoteId()));
                    movimiento.setLote(lote);
                } else {
                    Lote nuevoLote = new Lote();
                    nuevoLote.setBatchNumber(generateBatchNumber(producto));
                    nuevoLote.setProductionDate(LocalDate.now());
                    nuevoLote.setExpirationDate(LocalDate.now().plusMonths(6));

                    nuevoLote = loteRepo.save(nuevoLote);
                    movimiento.setLote(nuevoLote);
                }

                movimiento.setTransaccionAlmacen(transaccion);
                movimientos.add(movimiento);
            }
        }

        transaccion.setMovimientosTransaccion(movimientos);
        return transaccionAlmacenHeaderRepo.save(transaccion);
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

            // Para transacciones de tipo OCM (ingreso de materiales por orden de compra)
            // NO crear asiento automático, se hará manualmente desde el módulo de pagos
            if (ingresoOCM.getTipoEntidadCausante() == TransaccionAlmacen.TipoEntidadCausante.OCM) {
                ingresoOCM.setEstadoContable(TransaccionAlmacen.EstadoContable.PENDIENTE);
                log.info("Transacción de tipo OCM: el asiento contable se creará manualmente desde el módulo de pagos");
            } 
            // Para otros tipos de transacciones (que no son OCM)
            // SÍ crear asiento automático (este bloque no se ejecutará en este método específico,
            // pero se deja como referencia para futuros métodos que manejen otros tipos de transacciones)
            else {
                // Calcular el monto total para el asiento contable
                BigDecimal montoTotal = BigDecimal.ZERO;
                for (ItemOrdenCompra itemOrdenCompra : oc.getItemsOrdenCompra()) {
                    BigDecimal valorItem = BigDecimal.valueOf(itemOrdenCompra.getPrecioUnitario() * itemOrdenCompra.getCantidad());
                    montoTotal = montoTotal.add(valorItem);
                }

                try {
                    AsientoContable asiento = contabilidadService.registrarAsientoIngresoOCM(ingresoOCM, oc, montoTotal);
                    ingresoOCM.setAsientoContable(asiento);
                    ingresoOCM.setEstadoContable(TransaccionAlmacen.EstadoContable.CONTABILIZADA);
                    transaccionAlmacenHeaderRepo.save(ingresoOCM);
                    log.info("Asiento contable registrado con ID: " + asiento.getId());
                } catch (Exception e) {
                    log.error("Error al registrar asiento contable: " + e.getMessage(), e);
                    // No interrumpimos el flujo principal si falla la contabilidad
                }
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
                double nuevoCosto = getNuevoCosto(itemOrdenCompra, currentStockOpt, material);

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
            semiTerminado.setCosto(newCosto);

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
            terminado.setCosto(newCosto);

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


    private static double getNuevoCosto(ItemOrdenCompra itemOrdenCompra, Double currentStockOpt, Material material) {
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
        return Math.ceil(nuevoCosto);
    }

    /**
     * Creates a dispensation transaction for a production order.
     * This method handles the dispensation of materials from the warehouse to execute production orders.
     * 
     * @param dispensacionDTO The DTO containing the dispensation information
     * @return The created transaction
     */
    @Transactional
    public TransaccionAlmacen createDispensacion(DispensacionDTO dispensacionDTO) {
        // Obtain the production order
        OrdenProduccion ordenProduccion = ordenProduccionRepo.findById(dispensacionDTO.getOrdenProduccionId())
            .orElseThrow(() -> new RuntimeException("Orden de producción no encontrada con ID: " + dispensacionDTO.getOrdenProduccionId()));

        // Create the warehouse transaction
        TransaccionAlmacen transaccion = new TransaccionAlmacen();
        transaccion.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OP);
        transaccion.setIdEntidadCausante(ordenProduccion.getOrdenId());
        transaccion.setObservaciones(dispensacionDTO.getObservaciones());

        // Get the current user (this will depend on how authentication is handled)
        User user = userRepository.findById(Long.valueOf(dispensacionDTO.getUsuarioId()))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dispensacionDTO.getUsuarioId()));
        transaccion.setUser(user);

        // Create the movements
        List<Movimiento> movimientos = new ArrayList<>();
        for (DispensacionItemDTO item : dispensacionDTO.getItems()) {
            // Get the tracking
            OrdenSeguimiento seguimiento = ordenSeguimientoRepo.findById(item.getSeguimientoId())
                .orElseThrow(() -> new RuntimeException("Seguimiento no encontrado con ID: " + item.getSeguimientoId()));

            // Create the movement
            Movimiento movimiento = new Movimiento();
            movimiento.setCantidad(-item.getCantidad()); // Negative because it's an output
            movimiento.setProducto(seguimiento.getInsumo().getProducto());
            movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.CONSUMO);
            movimiento.setAlmacen(Movimiento.Almacen.GENERAL);
            movimiento.setTransaccionAlmacen(transaccion);

            // If a batch is specified, associate it
            if (item.getLoteId() != null) {
                Lote lote = loteRepo.findById(Long.valueOf(item.getLoteId()))
                    .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + item.getLoteId()));
                movimiento.setLote(lote);
            }

            movimientos.add(movimiento);

            // Update the tracking status if necessary
            if (item.isCompletarSeguimiento()) {
                seguimiento.setEstado(1); // Finished
                seguimiento.setFechaFinalizacion(LocalDateTime.now());
                ordenSeguimientoRepo.save(seguimiento);
            }
        }

        transaccion.setMovimientosTransaccion(movimientos);

        // Save the transaction
        return transaccionAlmacenHeaderRepo.save(transaccion);
    }

    /**
     * Creates an unplanned dispensation transaction without a production order.
     * This method checks if unplanned dispensation is allowed by system configuration.
     * 
     * @param dispensacionDTO The DTO containing the dispensation information
     * @return The created transaction
     */
    @Transactional
    public TransaccionAlmacen createDispensacionNoPlanificada(DispensacionNoPlanificadaDTO dispensacionDTO) {
        // Check if unplanned dispensation is allowed
        MasterDirective directive = masterDirectiveRepo.findByNombre("Permitir Consumo No Planificado")
            .orElseThrow(() -> new RuntimeException("Directiva de configuración no encontrada"));

        if (!"true".equalsIgnoreCase(directive.getValor())) {
            throw new RuntimeException("La dispensación no planificada no está permitida según la configuración del sistema");
        }

        // Create the warehouse transaction
        TransaccionAlmacen transaccion = new TransaccionAlmacen();
        transaccion.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OAA); // Usar OAA (Orden de Ajuste de Almacén)
        transaccion.setIdEntidadCausante(0); // No hay entidad causante específica, usamos 0 en lugar de null
        transaccion.setObservaciones(dispensacionDTO.getObservaciones());

        // Get the current user
        User user = userRepository.findById(Long.valueOf(dispensacionDTO.getUsuarioId()))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dispensacionDTO.getUsuarioId()));
        transaccion.setUser(user);

        // Create the movements
        List<Movimiento> movimientos = new ArrayList<>();
        for (DispensacionNoPlanificadaItemDTO item : dispensacionDTO.getItems()) {
            // Get the product
            Producto producto = productoRepo.findById(item.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getProductoId()));

            // Create the movement
            Movimiento movimiento = new Movimiento();
            movimiento.setCantidad(-item.getCantidad()); // Negative because it's an output
            movimiento.setProducto(producto);
            movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.CONSUMO);
            movimiento.setAlmacen(Movimiento.Almacen.GENERAL);
            movimiento.setTransaccionAlmacen(transaccion);

            // If a batch is specified, associate it
            if (item.getLoteId() != null) {
                Lote lote = loteRepo.findById(Long.valueOf(item.getLoteId()))
                    .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + item.getLoteId()));
                movimiento.setLote(lote);
            }

            movimientos.add(movimiento);
        }

        transaccion.setMovimientosTransaccion(movimientos);

        // Create accounting entry
        try {
            BigDecimal montoTotal = BigDecimal.ZERO;
            for (Movimiento movimiento : movimientos) {
                montoTotal = montoTotal.add(
                    BigDecimal.valueOf(Math.abs(movimiento.getCantidad()) * movimiento.getProducto().getCosto())
                );
            }

            AsientoContable asiento = contabilidadService.registrarAsientoConsumoNoPlanificado(transaccion, montoTotal);
            transaccion.setAsientoContable(asiento);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.CONTABILIZADA);
        } catch (Exception e) {
            // Log error but continue with the transaction
            log.error("Error al registrar asiento contable para dispensación no planificada: " + e.getMessage(), e);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.PENDIENTE);
        }

        // Save the transaction
        return transaccionAlmacenHeaderRepo.save(transaccion);
    }

    /**
     * Creates an unplanned backflush transaction without a production order.
     * This method checks if unplanned backflush is allowed by system configuration.
     * 
     * @param backflushDTO The DTO containing the backflush information
     * @return The created transaction
     */
    @Transactional
    public TransaccionAlmacen createBackflushNoPlanificado(BackflushNoPlanificadoDTO backflushDTO) {
        // Check if unplanned backflush is allowed
        MasterDirective directive = masterDirectiveRepo.findByNombre("Permitir Backflush No Planificado")
            .orElseThrow(() -> new RuntimeException("Directiva de configuración no encontrada"));

        if (!"true".equalsIgnoreCase(directive.getValor())) {
            throw new RuntimeException("El backflush no planificado no está permitido según la configuración del sistema");
        }

        // Get the product
        Producto producto = productoRepo.findById(backflushDTO.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + backflushDTO.getProductoId()));

        // Create the warehouse transaction
        TransaccionAlmacen transaccion = new TransaccionAlmacen();
        transaccion.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OAA); // Usar OAA (Orden de Ajuste de Almacén)
        transaccion.setIdEntidadCausante(0); // No hay entidad causante específica, usamos 0 en lugar de null
        transaccion.setObservaciones(backflushDTO.getObservaciones());

        // Get the current user
        User user = userRepository.findById(Long.valueOf(backflushDTO.getUsuarioId()))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + backflushDTO.getUsuarioId()));
        transaccion.setUser(user);

        // Create the movement
        Movimiento movimiento = new Movimiento();
        movimiento.setCantidad(backflushDTO.getCantidad()); // Positive because it's an input
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.BACKFLUSH);
        movimiento.setAlmacen(Movimiento.Almacen.GENERAL);
        movimiento.setTransaccionAlmacen(transaccion);

        // Generate a new batch for this product
        Lote lote = new Lote();
        lote.setBatchNumber(generateBatchNumber(producto));
        lote.setProductionDate(LocalDate.now());
        // No expiration date for now, as we don't have shelf life information
        loteRepo.save(lote);

        movimiento.setLote(lote);

        List<Movimiento> movimientos = new ArrayList<>();
        movimientos.add(movimiento);
        transaccion.setMovimientosTransaccion(movimientos);

        // Create accounting entry
        try {
            BigDecimal montoTotal = BigDecimal.valueOf(backflushDTO.getCantidad() * producto.getCosto());
            AsientoContable asiento = contabilidadService.registrarAsientoBackflushNoPlanificado(transaccion, producto, montoTotal);
            transaccion.setAsientoContable(asiento);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.CONTABILIZADA);
        } catch (Exception e) {
            // Log error but continue with the transaction
            log.error("Error al registrar asiento contable para backflush no planificado: " + e.getMessage(), e);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.PENDIENTE);
        }

        // Save the transaction
        return transaccionAlmacenHeaderRepo.save(transaccion);
    }

    /**
     * Recomienda lotes para dispensación de un producto específico.
     * Utiliza la lógica de selección de lotes por fecha de vencimiento (FEFO).
     * 
     * @param productoId ID del producto a dispensar
     * @param cantidadRequerida Cantidad total requerida
     * @return DTO con los items recomendados para dispensación
     */
    public DispensacionNoPlanificadaDTO recomendarLotesParaDispensacion(String productoId, double cantidadRequerida) {
        // Verificar que el producto existe
        Producto producto = productoRepo.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Obtener lotes con stock disponible para este producto
        List<Object[]> lotesConStock;
        try {
            // Intentar primero con la consulta JPQL
            lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdOrderByExpirationDate(productoId);
        } catch (Exception e) {
            // Si falla, usar la consulta SQL nativa como alternativa
            log.warn("Error al ejecutar consulta JPQL para lotes, usando SQL nativo: " + e.getMessage());
            lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdNative(productoId);
        }

        // Crear el DTO de respuesta
        DispensacionNoPlanificadaDTO dispensacionDTO = new DispensacionNoPlanificadaDTO();
        dispensacionDTO.setObservaciones("Recomendación automática de lotes para " + producto.getNombre());
        dispensacionDTO.setItems(new ArrayList<>());

        // Calcular cuánto tomar de cada lote
        double cantidadRestante = cantidadRequerida;

        for (Object[] result : lotesConStock) {
            if (cantidadRestante <= 0) {
                break;
            }

            try {
                Lote lote;
                Double stockDisponible;

                // Intentar procesar como resultado de consulta JPQL
                if (result[0] instanceof Lote) {
                    lote = (Lote) result[0];
                    stockDisponible = (Double) result[1];
                } 
                // Procesar como resultado de consulta SQL nativa
                else {
                    // Para SQL nativo, necesitamos buscar el lote por ID
                    Long loteId = null;

                    // El primer elemento podría ser un número (ID del lote)
                    if (result[0] instanceof Number) {
                        loteId = ((Number) result[0]).longValue();
                    } 
                    // O podría ser un mapa con los valores de las columnas
                    else if (result[0] instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) result[0];
                        loteId = ((Number) map.get("id")).longValue();
                    }

                    // Si no pudimos obtener el ID del lote, continuamos con el siguiente
                    if (loteId == null) {
                        log.warn("No se pudo obtener el ID del lote del resultado de la consulta");
                        continue;
                    }

                    // Buscar el lote por ID
                    Optional<Lote> optionalLote = loteRepo.findById(loteId);
                    if (!optionalLote.isPresent()) {
                        log.warn("No se encontró el lote con ID: " + loteId);
                        continue;
                    }

                    lote = optionalLote.get();

                    // El stock disponible podría estar en diferentes posiciones según la consulta
                    if (result.length > 1 && result[1] instanceof Number) {
                        stockDisponible = ((Number) result[1]).doubleValue();
                    } else {
                        log.warn("No se pudo obtener el stock disponible del resultado de la consulta");
                        continue;
                    }
                }

                double cantidadAUsar = Math.min(stockDisponible, cantidadRestante);

                // Crear un item de dispensación para este lote
                DispensacionNoPlanificadaItemDTO item = new DispensacionNoPlanificadaItemDTO();
                item.setProductoId(productoId);
                item.setCantidad(cantidadAUsar);
                item.setLoteId(lote.getId().intValue());

                dispensacionDTO.getItems().add(item);

                cantidadRestante -= cantidadAUsar;
            } catch (Exception e) {
                log.error("Error al procesar lote recomendado: " + e.getMessage(), e);
                // Continuamos con el siguiente lote
            }
        }

        // Verificar si se pudo satisfacer toda la cantidad requerida
        if (cantidadRestante > 0) {
            log.warn("No hay suficiente stock para satisfacer la cantidad requerida. Faltante: " + cantidadRestante);
            dispensacionDTO.setObservaciones(dispensacionDTO.getObservaciones() + 
                ". ADVERTENCIA: Stock insuficiente. Faltante: " + cantidadRestante);
        }

        return dispensacionDTO;
    }

    /**
     * Recomienda lotes para dispensación de múltiples productos.
     * Procesa cada solicitud individualmente y combina los resultados en un solo DTO.
     * 
     * @param items Lista de solicitudes de recomendación (producto y cantidad)
     * @return DTO con todos los items recomendados para dispensación
     */
    public DispensacionNoPlanificadaDTO recomendarLotesParaDispensacionMultiple(List<RecomendacionLotesRequestDTO> items) {
        // Crear el DTO de respuesta
        DispensacionNoPlanificadaDTO dispensacionDTO = new DispensacionNoPlanificadaDTO();
        dispensacionDTO.setObservaciones("Recomendación automática de lotes para múltiples productos");
        dispensacionDTO.setItems(new ArrayList<>());

        // Procesar cada solicitud
        for (RecomendacionLotesRequestDTO item : items) {
            try {
                // Obtener recomendación para este producto
                DispensacionNoPlanificadaDTO recomendacionIndividual = recomendarLotesParaDispensacion(
                        item.getProductoId(), item.getCantidad());

                // Agregar los items recomendados a la respuesta
                dispensacionDTO.getItems().addAll(recomendacionIndividual.getItems());

                // Si hubo alguna advertencia, agregarla a las observaciones
                if (recomendacionIndividual.getObservaciones().contains("ADVERTENCIA")) {
                    dispensacionDTO.setObservaciones(dispensacionDTO.getObservaciones() + 
                        "\n" + recomendacionIndividual.getObservaciones());
                }
            } catch (Exception e) {
                log.error("Error al procesar recomendación para producto " + item.getProductoId() + ": " + e.getMessage(), e);
                dispensacionDTO.setObservaciones(dispensacionDTO.getObservaciones() + 
                    "\nError al procesar producto " + item.getProductoId() + ": " + e.getMessage());
            }
        }

        return dispensacionDTO;
    }

    /**
     * Creates multiple unplanned backflush transactions without a production order.
     * This method checks if unplanned backflush is allowed by system configuration.
     * Allows specifying lots for each product.
     * 
     * @param backflushDTO The DTO containing the backflush information for multiple products
     * @return The created transaction
     */
    @Transactional
    public TransaccionAlmacen createBackflushMultipleNoPlanificado(BackflushMultipleNoPlanificadoDTO backflushDTO) {
        // Check if unplanned backflush is allowed
        MasterDirective directive = masterDirectiveRepo.findByNombre("Permitir Backflush No Planificado")
            .orElseThrow(() -> new RuntimeException("Directiva de configuración no encontrada"));

        if (!"true".equalsIgnoreCase(directive.getValor())) {
            throw new RuntimeException("El backflush no planificado no está permitido según la configuración del sistema");
        }

        // Create the warehouse transaction
        TransaccionAlmacen transaccion = new TransaccionAlmacen();
        transaccion.setTipoEntidadCausante(TransaccionAlmacen.TipoEntidadCausante.OAA); // Usar OAA (Orden de Ajuste de Almacén)
        transaccion.setIdEntidadCausante(0); // No hay entidad causante específica, usamos 0 en lugar de null
        transaccion.setObservaciones(backflushDTO.getObservaciones());

        // Get the current user
        User user = userRepository.findById(Long.valueOf(backflushDTO.getUsuarioId()))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + backflushDTO.getUsuarioId()));
        transaccion.setUser(user);

        // Create movements for each item
        List<Movimiento> movimientos = new ArrayList<>();
        BigDecimal montoTotalAsiento = BigDecimal.ZERO;

        for (BackflushNoPlanificadoItemDTO item : backflushDTO.getItems()) {
            // Get the product
            Producto producto = productoRepo.findById(item.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getProductoId()));

            // Create the movement
            Movimiento movimiento = new Movimiento();
            movimiento.setCantidad(item.getCantidad()); // Positive because it's an input
            movimiento.setProducto(producto);
            movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.BACKFLUSH);
            movimiento.setAlmacen(Movimiento.Almacen.GENERAL);
            movimiento.setTransaccionAlmacen(transaccion);

            // Handle lot assignment
            if (item.getLoteId() != null) {
                // Use existing lot if specified
                Lote lote = loteRepo.findById(Long.valueOf(item.getLoteId()))
                    .orElseThrow(() -> new RuntimeException("Lote no encontrado con ID: " + item.getLoteId()));
                movimiento.setLote(lote);
            } else {
                // Generate a new batch for this product
                Lote lote = new Lote();

                // Use provided batch number or generate one
                if (item.getBatchNumber() != null && !item.getBatchNumber().isEmpty()) {
                    lote.setBatchNumber(item.getBatchNumber());
                } else {
                    lote.setBatchNumber(generateBatchNumber(producto));
                }

                lote.setProductionDate(LocalDate.now());
                // No expiration date for now, as we don't have shelf life information
                loteRepo.save(lote);

                movimiento.setLote(lote);
            }

            movimientos.add(movimiento);

            // Accumulate total amount for accounting entry
            montoTotalAsiento = montoTotalAsiento.add(
                BigDecimal.valueOf(item.getCantidad() * producto.getCosto())
            );
        }

        transaccion.setMovimientosTransaccion(movimientos);

        // Create accounting entry
        try {
            // We'll use the first product for the accounting entry description
            Producto primerProducto = movimientos.get(0).getProducto();
            String descripcionAsiento = backflushDTO.getItems().size() > 1 
                ? "Ingreso múltiple de productos terminados" 
                : "Ingreso de producto terminado: " + primerProducto.getNombre();

            AsientoContable asiento = contabilidadService.registrarAsientoBackflushNoPlanificado(
                transaccion, primerProducto, montoTotalAsiento);
            transaccion.setAsientoContable(asiento);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.CONTABILIZADA);
        } catch (Exception e) {
            // Log error but continue with the transaction
            log.error("Error al registrar asiento contable para backflush múltiple no planificado: " + e.getMessage(), e);
            transaccion.setEstadoContable(TransaccionAlmacen.EstadoContable.PENDIENTE);
        }

        // Save the transaction
        return transaccionAlmacenHeaderRepo.save(transaccion);
    }

    /**
     * Obtiene los lotes disponibles para un producto específico.
     * Incluye información de fecha de vencimiento y cantidad disponible para cada lote.
     * 
     * @param productoId ID del producto
     * @return DTO con la información de lotes disponibles
     */
    public LoteDisponibleResponseDTO getLotesDisponiblesByProductoId(String productoId) {
        // Verificar que el producto existe
        Producto producto = productoRepo.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Obtener lotes con stock disponible para este producto
        List<Object[]> lotesConStock;
        try {
            // Intentar primero con la consulta JPQL
            lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdOrderByExpirationDate(productoId);
        } catch (Exception e) {
            // Si falla, usar la consulta SQL nativa como alternativa
            log.warn("Error al ejecutar consulta JPQL para lotes, usando SQL nativo: " + e.getMessage());
            lotesConStock = transaccionAlmacenRepo.findLotesWithStockByProductoIdNative(productoId);
        }

        // Crear el DTO de respuesta
        LoteDisponibleResponseDTO responseDTO = new LoteDisponibleResponseDTO();
        responseDTO.setProductoId(productoId);
        responseDTO.setNombreProducto(producto.getNombre());

        List<LoteRecomendadoDTO> lotesDisponibles = new ArrayList<>();

        // Procesar cada lote con stock
        for (Object[] result : lotesConStock) {
            try {
                Lote lote;
                Double stockDisponible;

                // Intentar procesar como resultado de consulta JPQL
                if (result[0] instanceof Lote) {
                    lote = (Lote) result[0];
                    stockDisponible = (Double) result[1];
                } 
                // Procesar como resultado de consulta SQL nativa
                else {
                    // Para SQL nativo, necesitamos buscar el lote por ID
                    Long loteId = null;

                    // El primer elemento podría ser un número (ID del lote)
                    if (result[0] instanceof Number) {
                        loteId = ((Number) result[0]).longValue();
                    } 
                    // O podría ser un mapa con los valores de las columnas
                    else if (result[0] instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) result[0];
                        loteId = ((Number) map.get("id")).longValue();
                    }

                    // Si no pudimos obtener el ID del lote, continuamos con el siguiente
                    if (loteId == null) {
                        log.warn("No se pudo obtener el ID del lote del resultado de la consulta");
                        continue;
                    }

                    // Buscar el lote por ID
                    Optional<Lote> optionalLote = loteRepo.findById(loteId);
                    if (!optionalLote.isPresent()) {
                        log.warn("No se encontró el lote con ID: " + loteId);
                        continue;
                    }

                    lote = optionalLote.get();

                    // El stock disponible podría estar en diferentes posiciones según la consulta
                    if (result.length > 1 && result[1] instanceof Number) {
                        stockDisponible = ((Number) result[1]).doubleValue();
                    } else {
                        log.warn("No se pudo obtener el stock disponible del resultado de la consulta");
                        continue;
                    }
                }

                // Crear DTO para este lote
                LoteRecomendadoDTO loteDTO = new LoteRecomendadoDTO();
                loteDTO.setLoteId(lote.getId());
                loteDTO.setBatchNumber(lote.getBatchNumber());
                loteDTO.setProductionDate(lote.getProductionDate());
                loteDTO.setExpirationDate(lote.getExpirationDate());
                loteDTO.setCantidadDisponible(stockDisponible);
                loteDTO.setCantidadRecomendada(0); // No estamos recomendando cantidades

                lotesDisponibles.add(loteDTO);
            } catch (Exception e) {
                log.error("Error al procesar lote disponible: " + e.getMessage(), e);
                // Continuamos con el siguiente lote
            }
        }

        responseDTO.setLotesDisponibles(lotesDisponibles);
        return responseDTO;
    }

    private Movimiento.TipoMovimiento resolveTipoMovimiento(AjusteItemDTO item) {
        if (item.getMotivo() != null) {
            try {
                return Movimiento.TipoMovimiento.valueOf(item.getMotivo().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // fallback basado en el signo de la cantidad
            }
        }

        return item.getCantidad() >= 0 ? Movimiento.TipoMovimiento.COMPRA : Movimiento.TipoMovimiento.BAJA;
    }

    public byte[] generateMovimientosExcel(MovimientoExcelRequestDTO dto) {
        LocalDateTime startDateTime = dto.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = dto.getEndDate().atTime(LocalTime.MAX);

        Double totalAcumulado = transaccionAlmacenRepo.findTotalCantidadByProductoIdAndFechaMovimientoBefore(dto.getProductoId(), startDateTime);
        totalAcumulado = totalAcumulado != null ? totalAcumulado : 0.0;

        List<Movimiento> movimientos = transaccionAlmacenRepo
                .findByProducto_ProductoIdAndFechaMovimientoBetweenOrderByFechaMovimientoAsc(
                        dto.getProductoId(), startDateTime, endDateTime);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Movimientos");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Total hasta " + dto.getStartDate());
            header.createCell(1).setCellValue(totalAcumulado);

            int rowIdx = 1;
            for (Movimiento mov : movimientos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(mov.getFechaMovimiento().toString());
                row.createCell(1).setCellValue(mov.getTipoMovimiento().name());
                row.createCell(2).setCellValue(mov.getCantidad());
                row.createCell(3).setCellValue(mov.getAlmacen() != null ? mov.getAlmacen().name() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }
}
