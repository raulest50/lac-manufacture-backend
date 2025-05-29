package lacosmetics.planta.lacmanufacture.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public OrdenCompraMateriales updateEstadoOrdenCompra(int ordenCompraId, UpdateEstadoOrdenCompraRequest ue) {
        OrdenCompraMateriales orden = ordenCompraRepo.findById(ordenCompraId)
                .orElseThrow(() -> new RuntimeException("OrdenCompraMateriales not found with id: " + ordenCompraId));

        // Actualizar el estado
        orden.setEstado(ue.getNewEstado());

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
                        if (ue.getOCMpdf() != null && !ue.getOCMpdf().isEmpty()) {
                            try {
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

                                if (emailProveedor != null) {
                                    // Preparar el asunto y cuerpo del correo
                                    String subject = "Actualización de Orden de Compra #" + ordenCompraId;
                                    String text = "Estimado proveedor,\n\n" +
                                            "La orden de compra #" + ordenCompraId + " ha sido actualizada a estado 'pendiente ingreso almacén'.\n" +
                                            "Adjuntamos el documento PDF con los detalles de la orden.\n\n" +
                                            "Saludos cordiales,\n" +
                                            "LA Cosmetics";

                                    // Enviar el correo con el PDF adjunto
                                    emailService.sendEmailWithAttachment(
                                            emailProveedor,
                                            subject,
                                            text,
                                            ue.getOCMpdf()
                                    );

                                    log.info("Email sent to provider {} with order details for order ID: {}", emailProveedor, ordenCompraId);
                                } else {
                                    log.warn("No email found for provider with ID: {}", proveedor.getId());
                                }
                            } catch (MessagingException | IOException e) {
                                // Loguear el error pero permitir que la actualización de estado continúe
                                // No queremos que un error en el envío de correo impida la actualización
                                log.error("Error al enviar correo al proveedor: {}", e.getMessage(), e);
                            }
                        } else {
                            log.warn("No se proporcionó archivo PDF para enviar por email para la orden: {}", ordenCompraId);
                        }
                        break;

                    case WHATSAPP:
                        // TODO: Implementar envío por WhatsApp en el futuro
                        log.info("Envío por WhatsApp seleccionado para orden {}, esta funcionalidad será implementada próximamente", ordenCompraId);
                        throw new UnsupportedOperationException("El envío por WhatsApp aún no está implementado");

                    default:
                        log.warn("Tipo de envío no reconocido para la orden: {}", ordenCompraId);
                }
            } else {
                // Si no se especificó tipo de envío, usar el comportamiento predeterminado (email)
                log.warn("No se especificó tipo de envío para la orden: {}, usando email por defecto", ordenCompraId);

                // Verificar si hay un PDF adjunto
                if (ue.getOCMpdf() != null && !ue.getOCMpdf().isEmpty()) {
                    try {
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

                        if (emailProveedor != null) {
                            // Preparar el asunto y cuerpo del correo
                            String subject = "Actualización de Orden de Compra #" + ordenCompraId;
                            String text = "Estimado proveedor,\n\n" +
                                    "La orden de compra #" + ordenCompraId + " ha sido actualizada a estado 'pendiente ingreso almacén'.\n" +
                                    "Adjuntamos el documento PDF con los detalles de la orden.\n\n" +
                                    "Saludos cordiales,\n" +
                                    "LA Cosmetics";

                            // Enviar el correo con el PDF adjunto
                            emailService.sendEmailWithAttachment(
                                    emailProveedor,
                                    subject,
                                    text,
                                    ue.getOCMpdf()
                            );

                            log.info("Email sent to provider {} with order details for order ID: {}", emailProveedor, ordenCompraId);
                        } else {
                            log.warn("No email found for provider with ID: {}", proveedor.getId());
                        }
                    } catch (MessagingException | IOException e) {
                        // Loguear el error pero permitir que la actualización de estado continúe
                        // No queremos que un error en el envío de correo impida la actualización
                        log.error("Error al enviar correo al proveedor: {}", e.getMessage(), e);
                    }
                }
            }
        }

        return ordenCompraRepo.save(orden);
    }

    public OrdenCompraMateriales getOrdenCompraByOrdenCompraIdAndEstado(Integer ordenCompraId, int estado) {
        return ordenCompraRepo.findByOrdenCompraIdAndEstado(ordenCompraId, estado)
                .orElseThrow(() -> new RuntimeException("OrdenCompraMateriales not found with OrdenCompraId: "
                        + ordenCompraId + " and estado = " + estado));
    }

}
