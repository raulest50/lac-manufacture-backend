package exotic.app.planta.model.commons.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for email requests with multiple recipients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleRecipientsEmailRequestDTO {
    private String[] to;
    private String subject;
    private String text;
}
