package lacosmetics.planta.lacmanufacture.service.compras;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.compras.*;
import lacosmetics.planta.lacmanufacture.model.dto.compra.materiales.UpdateEstadoOrdenCompraRequest;
import lacosmetics.planta.lacmanufacture.repo.compras.FacturaCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.OrdenCompraRepo;
import lacosmetics.planta.lacmanufacture.repo.compras.ProveedorRepo;
import lacosmetics.planta.lacmanufacture.repo.inventarios.TransaccionAlmacenRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.MaterialRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.SemiTerminadoRepo;
import lacosmetics.planta.lacmanufacture.repo.producto.TerminadoRepo;
import lacosmetics.planta.lacmanufacture.service.commons.EmailService;
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
        String text = "Estimado proveedor: " + orden.getProveedor() + ",\n\n" +
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
                            enviarCorreoOrdenCompraProveedor(orden, ue.getOCMpdf());
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
                    enviarCorreoOrdenCompraProveedor(orden, ue.getOCMpdf());
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

}
