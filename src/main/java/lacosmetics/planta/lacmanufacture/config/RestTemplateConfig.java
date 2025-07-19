package lacosmetics.planta.lacmanufacture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para RestTemplate
 * Proporciona un bean de RestTemplate para realizar peticiones HTTP a APIs externas
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Crea y configura un bean de RestTemplate
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}