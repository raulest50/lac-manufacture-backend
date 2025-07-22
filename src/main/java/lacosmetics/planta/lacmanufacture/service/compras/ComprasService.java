package lacosmetics.planta.lacmanufacture.service.compras;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.compras.*;
import lacosmetics.planta.lacmanufacture.model.dto.compra.materiales.UpdateEstadoOrdenCompraRequest;
import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.repo.compras.FacturaCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.service.commons.EmailService;
import lacosmetics.planta.lacmanufacture.model.users.Acceso;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComprasService {

    private final FacturaCompraRepo facturaCompraRepo;
    private final TransaccionAlmacenRepo transaccionAlmacenRepo;
    private final ProveedorRepo proveedorRepo;
    private final MaterialRepo materialRepo;
    private final SemiTerminadoRepo semiTerminadoRepo;
    private final TerminadoRepo terminadoRepo;

    private final OrdenCompraRepo ordenCompraRepo;

    private final EmailService emailService;

    private final UserRepository userRepository;

    /**
     *
     * Compras
     *
     */



    public Page<FacturaCompra> getComprasByProveedorAndDate(int proveedorId, String date1, String date2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDate = LocalDate.parse(date1).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(date2).atTime(LocalTime.MAX);
        return facturaCompraRepo.findByProveedorIdAndFechaCompraBetween(proveedorId, startDate, endDate, pageable);
    }

    public List<ItemFacturaCompra> getItemsByCompraId(int compraId) {
        FacturaCompra facturaCompra = facturaCompraRepo.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra not found with id: " + compraId));
        return facturaCompra.getItemsCompra();
    }


    /**
     *
     * Ordenes de Compra
     *
     */
    public OrdenCompraMateriales saveOrdenCompra(OrdenCompraMateriales ordenCompraMateriales) {
        // Verify the Proveedor exists
        Optional<Proveedor> optProveedor = proveedorRepo.findById(ordenCompraMateriales.getProveedor().getId());
        if (!optProveedor.isPresent()) {
            throw new RuntimeException("Proveedor not found with ID: " + ordenCompraMateriales.getProveedor().getId());
        }
        ordenCompraMateriales.setProveedor(optProveedor.get());

        // For each ItemOrdenCompra, set the back‑reference and initialize check fields to 0
        for (ItemOrdenCompra item : ordenCompraMateriales.getItemsOrdenCompra()) {
            item.setOrdenCompraMateriales(ordenCompraMateriales);
            if(item.getCantidadCorrecta() == 0) item.setCantidadCorrecta(0);
            if(item.getPrecioCorrecto() == 0) item.setPrecioCorrecto(0);
        }

        // Save and return the OrdenCompraMateriales
        return ordenCompraRepo.save(ordenCompraMateriales);
    }

    public Page<OrdenCompraMateriales> getOrdenesCompraByDateAndEstado(String date1, String date2, String estados, int page, int size) {
        // Parse the date strings
        LocalDateTime startDate = LocalDate.parse(date1).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(date2).atTime(LocalTime.MAX);

        // Parse the estados string into a list of integers
        List<Integer> estadoList = Arrays.stream(estados.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        return ordenCompraRepo.findByFechaEmisionBetweenAndEstadoIn(startDate, endDate, estadoList, pageable);
    }

    public OrdenCompraMateriales cancelOrdenCompra(int ordenCompraId) {
        OrdenCompraMateriales orden = ordenCompraRepo.findById(ordenCompraId)
                .orElseThrow(() -> new RuntimeException("OrdenCompraMateriales not found with id: " + ordenCompraId));
        orden.setEstado(-1);
        return ordenCompraRepo.save(orden);
    }

    /**
     * Envía un correo electrónico al proveedor con la orden de compra adjunta.
     * 
     * @param orden La orden de compra a enviar
     * @param pdfAttachment El archivo PDF adjunto con los detalles de la orden
     * @throws MessagingException Si hay un error al enviar el correo
     * @throws IOException Si hay un error al procesar el archivo adjunto
     * @throws RuntimeException Si no se encuentra un email para el proveedor
     */
    public void enviarCorreoOrdenCompraProveedor(OrdenCompraMateriales orden, MultipartFile pdfAttachment) 
            throws MessagingException, IOException {

        if (pdfAttachment == null || pdfAttachment.isEmpty()) {
            throw new RuntimeException("No se proporcionó archivo PDF para enviar por email para la orden: " + orden.getOrdenCompraId());
        }

        // Obtener el proveedor y su información de contacto
        Proveedor proveedor = orden.getProveedor();

        // Buscar el email del proveedor en la lista de contactos
        String emailProveedor = null;
        for (Map<String, Object> contacto : proveedor.getContactos()) {
            if (contacto.containsKey("email")) {
                emailProveedor = (String) contacto.get("email");
                break;
            }
        }

        if (emailProveedor == null) {
            throw new RuntimeException("No se encontró email para el proveedor con ID: " + proveedor.getId());
        }

        // Preparar el asunto y cuerpo del correo
        String subject = "No Reply - Orden de Compra Exotic Expert #" + orden.getOrdenCompraId();
        String text = "Estimado proveedor: " + orden.getProveedor().getNombre() + ",\n\n" +
                "Por medio de la presente le hacemos llegar la orden de compra #" + orden.getOrdenCompraId() +
                "correspondiente a los productos/servicios detallados en el documento adjunto.\n" +
                "Le agradeceremos confirmar la recepción de esta orden y, en caso de ser necesario," +
                "informarnos sobre el tiempo estimado de entrega o cualquier observación relevante.\n\n" +
                "Quedamos atentos a su confirmación y agradecemos de antemano su atención y colaboración.\n\n" +
                "Saludos cordiales,\n" +
                "Exotic Expert - Departamento de Compras";

        // Enviar el correo con el PDF adjunto
        emailService.sendEmailWithAttachment(
                emailProveedor,
                subject,
                text,
                pdfAttachment
        );

        log.info("Email sent to provider {} with order details for order ID: {}", emailProveedor, orden.getOrdenCompraId());
    }

    /**
     * Envía un correo electrónico al proveedor con copia a otros destinatarios.
     * Funciona igual que {@link #enviarCorreoOrdenCompraProveedor(OrdenCompraMateriales, MultipartFile)}
     * pero permite especificar destinatarios en copia (CC).
     *
     * @param orden       La orden de compra a enviar
     * @param pdfAttachment El archivo PDF adjunto con los detalles de la orden
     * @param ccEmails    Lista de correos electrónicos que recibirán copia
     * @throws MessagingException Si hay un error al enviar el correo
     * @throws IOException        Si hay un error al procesar el archivo adjunto
     * @throws RuntimeException   Si no se encuentra un email para el proveedor
     */
    public void enviarCorreoOrdenCompraProveedor_wCC(OrdenCompraMateriales orden, MultipartFile pdfAttachment, List<String> ccEmails)
            throws MessagingException, IOException {

        if (pdfAttachment == null || pdfAttachment.isEmpty()) {
            throw new RuntimeException("No se proporcionó archivo PDF para enviar por email para la orden: " + orden.getOrdenCompraId());
        }

        // Obtener el proveedor y su información de contacto
        Proveedor proveedor = orden.getProveedor();

        // Buscar el email del proveedor en la lista de contactos
        String emailProveedor = null;
        for (Map<String, Object> contacto : proveedor.getContactos()) {
            if (contacto.containsKey("email")) {
                emailProveedor = (String) contacto.get("email");
                break;
            }
        }

        if (emailProveedor == null) {
            throw new RuntimeException("No se encontró email para el proveedor con ID: " + proveedor.getId());
        }

        String subject = "No Reply - Orden de Compra Exotic Expert #" + orden.getOrdenCompraId();
        String text = "Estimado proveedor: " + orden.getProveedor().getNombre() + ",\n\n" +
                "Por medio de la presente le hacemos llegar la orden de compra #" + orden.getOrdenCompraId() +
                "correspondiente a los productos/servicios detallados en el documento adjunto.\n" +
                "Le agradeceremos confirmar la recepción de esta orden y, en caso de ser necesario," +
                "informarnos sobre el tiempo estimado de entrega o cualquier observación relevante.\n\n" +
                "Quedamos atentos a su confirmación y agradecemos de antemano su atención y colaboración.\n\n" +
                "Saludos cordiales,\n" +
                "Exotic Expert - Departamento de Compras";

        emailService.sendEmailWithAttachmentAndCC(
                emailProveedor,
                ccEmails.toArray(new String[0]),
                subject,
                text,
                pdfAttachment
        );

        log.info("Email sent to provider {} with CC {} for order ID: {}", emailProveedor, ccEmails, orden.getOrdenCompraId());
    }

    /**
     * Obtiene los correos electrónicos de los usuarios con acceso al módulo PRODUCCION
     * y nivel 2.
     *
     * @return lista de correos electrónicos
     */
    public List<String> getEmailsUsuariosProduccionNivel2() {
        return userRepository.findAll().stream()
                .filter(u -> u.getEmail() != null && !u.getEmail().isEmpty())
                .filter(u -> u.getAccesos().stream()
                        .anyMatch(a -> a.getModuloAcceso() == Acceso.Modulo.PRODUCCION && a.getNivel() == 2))
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdenCompraMateriales updateEstadoOrdenCompra(int ordenCompraId, UpdateEstadoOrdenCompraRequest ue) {
        OrdenCompraMateriales orden = ordenCompraRepo.findById(ordenCompraId)
                .orElseThrow(() -> new RuntimeException("OrdenCompraMateriales not found with id: " + ordenCompraId));

        // Si el nuevo estado es 2 y estamos cambiando desde estado 1, manejar según el tipo de envío
        if (ue.getNewEstado() == 2 && orden.getEstado() == 1) {
            // Verificar el tipo de envío seleccionado
            if (ue.getTipoEnvio() != null) {
                switch (ue.getTipoEnvio()) {
                    case MANUAL:
                        // Para envío manual, solo se actualiza el estado sin procesar el PDF
                        log.info("Orden de compra {} actualizada manualmente a estado 2", ordenCompraId);
                        break;

                    case EMAIL:
                        // Para envío por email, verificar que exista el PDF y enviarlo
                        try {
                            List<String> ccEmails = getEmailsUsuariosProduccionNivel2();
                            if (ccEmails.isEmpty()) {
                                enviarCorreoOrdenCompraProveedor(orden, ue.getOCMpdf());
                            } else {
                                enviarCorreoOrdenCompraProveedor_wCC(orden, ue.getOCMpdf(), ccEmails);
                            }
                        } catch (MessagingException | IOException e) {
                            // Lanzar una excepción para que la transacción se revierta
                            log.error("Error al enviar correo al proveedor: {}", e.getMessage(), e);
                            throw new RuntimeException("Error al enviar correo al proveedor: " + e.getMessage(), e);
                        } catch (RuntimeException e) {
                            // Propagar la excepción para que la transacción se revierta
                            log.error(e.getMessage());
                            throw e;
                        }
                        break;

                    case WHATSAPP:
                        // TODO: Implementar envío por WhatsApp en el futuro
                        log.info("Envío por WhatsApp seleccionado para orden {}, esta funcionalidad será implementada próximamente", ordenCompraId);
                        throw new UnsupportedOperationException("El envío por WhatsApp aún no está implementado");

                    default:
                        log.warn("Tipo de envío no reconocido para la orden: {}", ordenCompraId);
                        throw new RuntimeException("Tipo de envío no reconocido para la orden: " + ordenCompraId);
                }
            } else {
                // Si no se especificó tipo de envío, usar el comportamiento predeterminado (email)
                log.warn("No se especificó tipo de envío para la orden: {}, usando email por defecto", ordenCompraId);

                try {
                    List<String> ccEmails = getEmailsUsuariosProduccionNivel2();
                    if (ccEmails.isEmpty()) {
                        enviarCorreoOrdenCompraProveedor(orden, ue.getOCMpdf());
                    } else {
                        enviarCorreoOrdenCompraProveedor_wCC(orden, ue.getOCMpdf(), ccEmails);
                    }
                } catch (MessagingException | IOException e) {
                    // Lanzar una excepción para que la transacción se revierta
                    log.error("Error al enviar correo al proveedor: {}", e.getMessage(), e);
                    throw new RuntimeException("Error al enviar correo al proveedor: " + e.getMessage(), e);
                } catch (RuntimeException e) {
                    // Propagar la excepción para que la transacción se revierta
                    log.error(e.getMessage());
                    throw e;
                }
            }
        }

        // Actualizar el estado solo después de que todo el proceso haya sido exitoso
        orden.setEstado(ue.getNewEstado());
        return ordenCompraRepo.save(orden);
    }

    public OrdenCompraMateriales getOrdenCompraByOrdenCompraIdAndEstado(Integer ordenCompraId, int estado) {
        return ordenCompraRepo.findByOrdenCompraIdAndEstado(ordenCompraId, estado)
                .orElseThrow(() -> new RuntimeException("OrdenCompraMateriales not found with OrdenCompraId: "
                        + ordenCompraId + " and estado = " + estado));
    }

    /**
     * Actualiza una orden de compra existente.
     * 
     * @param ordenCompraId ID de la orden de compra a actualizar
     * @param ordenCompraMateriales Objeto con los datos actualizados
     * @return La orden de compra actualizada
     * @throws RuntimeException Si la orden no existe o hay algún error en la actualización
     */
    @Transactional
    public OrdenCompraMateriales updateOrdenCompra(int ordenCompraId, OrdenCompraMateriales ordenCompraMateriales) {
        // Verificar que la orden de compra existe
        OrdenCompraMateriales ordenExistente = ordenCompraRepo.findById(ordenCompraId)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada con ID: " + ordenCompraId));

        // Verificar que el proveedor existe
        Proveedor proveedor = proveedorRepo.findById(ordenCompraMateriales.getProveedor().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + 
                        ordenCompraMateriales.getProveedor().getId()));

        // Actualizar los campos básicos de la orden
        ordenExistente.setFechaVencimiento(ordenCompraMateriales.getFechaVencimiento());
        ordenExistente.setProveedor(proveedor);
        ordenExistente.setCondicionPago(ordenCompraMateriales.getCondicionPago());
        ordenExistente.setTiempoEntrega(ordenCompraMateriales.getTiempoEntrega());
        ordenExistente.setPlazoPago(ordenCompraMateriales.getPlazoPago());
        ordenExistente.setSubTotal(ordenCompraMateriales.getSubTotal());
        ordenExistente.setIvaCOP(ordenCompraMateriales.getIvaCOP());
        ordenExistente.setTotalPagar(ordenCompraMateriales.getTotalPagar());

        // Actualizar los nuevos atributos de divisa y TRM
        ordenExistente.setDivisas(ordenCompraMateriales.getDivisas());
        ordenExistente.setTrm(ordenCompraMateriales.getTrm());

        // No modificamos el estado ni la fecha de emisión aquí
        // Si se necesita cambiar el estado, se debe usar updateEstadoOrdenCompra

        // Eliminar todos los items existentes y agregar los nuevos
        ordenExistente.getItemsOrdenCompra().clear();

        // Agregar los nuevos items
        for (ItemOrdenCompra item : ordenCompraMateriales.getItemsOrdenCompra()) {
            // Verificar que el material existe
            Material material = materialRepo.findById(item.getMaterial().getProductoId())
                    .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + 
                            item.getMaterial().getProductoId()));

            // Crear un nuevo item y configurarlo
            ItemOrdenCompra nuevoItem = new ItemOrdenCompra();
            nuevoItem.setOrdenCompraMateriales(ordenExistente);
            nuevoItem.setMaterial(material);
            nuevoItem.setCantidad(item.getCantidad());
            nuevoItem.setPrecioUnitario(item.getPrecioUnitario());
            nuevoItem.setIvaCOP(item.getIvaCOP());
            nuevoItem.setSubTotal(item.getSubTotal());
            nuevoItem.setCantidadCorrecta(item.getCantidadCorrecta());
            nuevoItem.setPrecioCorrecto(item.getPrecioCorrecto());

            // Agregar el item a la orden
            ordenExistente.getItemsOrdenCompra().add(nuevoItem);
        }

        // Guardar y retornar la orden actualizada
        return ordenCompraRepo.save(ordenExistente);
    }

}
