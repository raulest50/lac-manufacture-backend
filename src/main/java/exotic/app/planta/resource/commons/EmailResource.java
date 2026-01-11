package exotic.app.planta.resource.commons;

import jakarta.mail.MessagingException;
import exotic.app.planta.model.commons.dto.email.EmailRequestDTO;
import exotic.app.planta.model.commons.dto.email.HtmlEmailRequestDTO;
import exotic.app.planta.model.commons.dto.email.MultipleRecipientsEmailRequestDTO;
import exotic.app.planta.service.commons.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for sending emails.
 * This controller provides endpoints for sending different types of emails:
 * - Simple text emails
 * - HTML emails
 * - Emails with attachments
 * - Emails to multiple recipients
 */
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Slf4j
public class EmailResource {

    private final EmailService emailService;

    /**
     * Send a simple text email
     *
     * @param request The email request containing recipient, subject, and text
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/simple")
    public ResponseEntity<String> sendSimpleEmail(@RequestBody EmailRequestDTO request) {
        try {
            log.info("Received request to send simple email to: {}", request.getTo());
            emailService.sendSimpleEmail(request.getTo(), request.getSubject(), request.getText());
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            log.error("Error sending simple email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send an HTML email
     *
     * @param request The email request containing recipient, subject, and HTML content
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/html")
    public ResponseEntity<String> sendHtmlEmail(@RequestBody HtmlEmailRequestDTO request) {
        try {
            log.info("Received request to send HTML email to: {}", request.getTo());
            emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getHtmlContent());
            return ResponseEntity.ok("HTML email sent successfully");
        } catch (MessagingException e) {
            log.error("Error sending HTML email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send HTML email: " + e.getMessage());
        }
    }

    /**
     * Send an email with attachment
     *
     * @param to        The recipient email address
     * @param subject   The email subject
     * @param text      The email body text
     * @param attachment The attachment file
     * @return ResponseEntity with success message or error
     */
    @PostMapping(value = "/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text,
            @RequestParam("attachment") MultipartFile attachment
    ) {
        try {
            log.info("Received request to send email with attachment to: {}", to);
            emailService.sendEmailWithAttachment(to, subject, text, attachment);
            return ResponseEntity.ok("Email with attachment sent successfully");
        } catch (MessagingException | IOException e) {
            log.error("Error sending email with attachment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email with attachment: " + e.getMessage());
        }
    }

    /**
     * Send an email to multiple recipients
     *
     * @param request The email request containing recipients, subject, and text
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/multiple-recipients")
    public ResponseEntity<String> sendEmailToMultipleRecipients(@RequestBody MultipleRecipientsEmailRequestDTO request) {
        try {
            log.info("Received request to send email to multiple recipients: {}", (Object) request.getTo());
            emailService.sendEmailToMultipleRecipients(request.getTo(), request.getSubject(), request.getText());
            return ResponseEntity.ok("Email sent successfully to multiple recipients");
        } catch (Exception e) {
            log.error("Error sending email to multiple recipients: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email to multiple recipients: " + e.getMessage());
        }
    }
}