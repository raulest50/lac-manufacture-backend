package exotic.app.planta.model.commons.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for HTML email requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HtmlEmailRequestDTO {
    private String to;
    private String subject;
    private String htmlContent;
}
