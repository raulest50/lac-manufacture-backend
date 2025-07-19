package lacosmetics.planta.lacmanufacture.service.commons;

import lacosmetics.planta.lacmanufacture.model.commons.TRM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Servicio para consultar la Tasa Representativa del Mercado (TRM)
 * de la Superintendencia Financiera de Colombia
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TRMService {

    private static final String TRM_API_URL = "https://www.datos.gov.co/resource/32sa-8pi3.json";
    private final RestTemplate restTemplate;

    /**
     * Obtiene la TRM actual desde la API de datos abiertos de Colombia
     * @return Objeto TRM con la información actual
     */
    public TRM getTRMActual() {
        log.info("Consultando TRM actual");
        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(TRM_API_URL + "?$limit=1&$order=vigenciadesde%20DESC", Map[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().length > 0) {
                Map<String, Object> trmData = response.getBody()[0];
                
                TRM trm = new TRM();
                trm.setFecha(LocalDate.parse((String) trmData.get("vigenciadesde"), DateTimeFormatter.ISO_DATE_TIME));
                trm.setValor(new BigDecimal((String) trmData.get("valor")));
                trm.setUnidad((String) trmData.get("unidad"));
                trm.setVigenciaDesde((String) trmData.get("vigenciadesde"));
                trm.setVigenciaHasta((String) trmData.get("vigenciahasta"));
                
                log.info("TRM actual obtenida correctamente: {}", trm);
                return trm;
            } else {
                log.error("Error al consultar TRM: respuesta vacía o con error");
                throw new RuntimeException("No se pudo obtener la TRM actual");
            }
        } catch (Exception e) {
            log.error("Error al consultar TRM", e);
            throw new RuntimeException("Error al consultar TRM: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la TRM para una fecha específica
     * @param fecha Fecha para la cual se requiere la TRM
     * @return Objeto TRM con la información para la fecha especificada
     */
    public TRM getTRMPorFecha(LocalDate fecha) {
        log.info("Consultando TRM para la fecha: {}", fecha);
        String fechaStr = fecha.format(DateTimeFormatter.ISO_DATE);
        
        try {
            String query = "?$where=vigenciadesde%20%3C=%20'" + fechaStr + 
                           "T00:00:00.000'%20AND%20vigenciahasta%20%3E=%20'" + 
                           fechaStr + "T00:00:00.000'";
            
            ResponseEntity<Map[]> response = restTemplate.getForEntity(TRM_API_URL + query, Map[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().length > 0) {
                Map<String, Object> trmData = response.getBody()[0];
                
                TRM trm = new TRM();
                trm.setFecha(fecha);
                trm.setValor(new BigDecimal((String) trmData.get("valor")));
                trm.setUnidad((String) trmData.get("unidad"));
                trm.setVigenciaDesde((String) trmData.get("vigenciadesde"));
                trm.setVigenciaHasta((String) trmData.get("vigenciahasta"));
                
                log.info("TRM para fecha {} obtenida correctamente: {}", fecha, trm);
                return trm;
            } else {
                log.error("Error al consultar TRM para fecha {}: respuesta vacía o con error", fecha);
                throw new RuntimeException("No se pudo obtener la TRM para la fecha " + fecha);
            }
        } catch (Exception e) {
            log.error("Error al consultar TRM para fecha {}", fecha, e);
            throw new RuntimeException("Error al consultar TRM para fecha " + fecha + ": " + e.getMessage(), e);
        }
    }
}