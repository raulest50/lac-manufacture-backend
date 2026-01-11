package exotic.app.planta.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración centralizada para Jackson ObjectMapper.
 * Configura el manejo adecuado de tipos de fecha/hora de Java.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configura un ObjectMapper con soporte para tipos de fecha/hora de Java.
     * 
     * @return ObjectMapper configurado
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Registrar el módulo JavaTime para manejar LocalDateTime y otros tipos de fecha/hora
        objectMapper.registerModule(new JavaTimeModule());
        
        // Opcional: Deshabilitar la serialización de fechas como timestamps (usar formato ISO-8601)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
}