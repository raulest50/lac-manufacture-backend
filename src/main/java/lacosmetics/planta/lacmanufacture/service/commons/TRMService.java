package lacosmetics.planta.lacmanufacture.service.commons;

import com.socrata.api.Soda2Consumer;
import com.socrata.exceptions.SodaError;
import com.socrata.model.soql.OrderByClause;
import com.socrata.model.soql.SortOrder;
import com.socrata.model.soql.SoqlQuery;
import com.socrata.builders.SoqlQueryBuilder;
import jakarta.annotation.PostConstruct;
import lacosmetics.planta.lacmanufacture.model.commons.divisas.TRM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class TRMService {

    private static final String DOMAIN     = "www.datos.gov.co";
    private static final String DATASET_ID = "32sa-8pi3";

    private final Soda2Consumer consumer;
    private final AtomicReference<TRM> cache = new AtomicReference<>();

    /**
     * Constructor que inicializa el consumer SODA de forma anónima (sin token)
     */
    public TRMService() {
        // Inicialización anónima (sin app token)
        this.consumer = Soda2Consumer.newConsumer(DOMAIN);
        log.warn("Requests made without an app_token will be subject to strict throttling limits.");
    }

    /** Carga inicial de la TRM en caché al arrancar la app */
    @PostConstruct
    public void init() {
        refreshCache();
    }

    /** Refresca la caché todos los días a la medianoche */
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshCache() {
        try {
            TRM latest = fetchLatest();
            cache.set(latest);
            log.info("✅ Caché de TRM actualizada: {}", latest);
        } catch (Exception ex) {
            log.error("❌ Error refrescando caché TRM", ex);
        }
    }

    /** Devuelve la TRM actual desde caché (o al vuelo si está vacía) */
    public TRM getTRMActual() {
        TRM trm = cache.get();
        if (trm == null) {
            log.info("Cache vacía, obteniendo TRM al vuelo");
            trm = fetchLatest();
            cache.set(trm);
        }
        return trm;
    }

    /** Devuelve la TRM para una fecha específica */
    public TRM getTRMPorFecha(LocalDate fecha) {
        log.info("Consultando TRM para la fecha: {}", fecha);
        String fechaStr = fecha.toString();  // yyyy-MM-dd
        // SoQL: vigenciadesde ≤ fecha AND vigenciahasta ≥ fecha
        String where = String.format(
                "vigenciadesde <= '%1$s' AND vigenciahasta >= '%1$s'",
                fechaStr
        );

        SoqlQuery query = new SoqlQueryBuilder()
                .setWhereClause(where)
                .setLimit(1)
                .build();

        try {
            @SuppressWarnings("unchecked")
            List<Object> rawResult = consumer.query(DATASET_ID, query, Soda2Consumer.HASH_RETURN_TYPE);

            // Convert the raw result to the expected type
            List<Map<String, Object>> rows = (List<Map<String, Object>>) (List<?>) rawResult;

            if (rows.isEmpty()) {
                throw new RuntimeException("No se encontró TRM para la fecha " + fecha);
            }
            // Mapeamos el primer registro
            return mapToTrm(rows.get(0), fecha);
        } catch (SodaError | InterruptedException e) {
            log.error("Error consultando TRM para la fecha {}: {}", fecha, e.getMessage(), e);
            throw new RuntimeException("Error consultando TRM para la fecha " + fecha, e);
        }
    }

    /** Llamada real a Socrata para obtener el registro más reciente */
    private TRM fetchLatest() {
        SoqlQuery query = new SoqlQueryBuilder()
                .setLimit(1)
                .addOrderByPhrase(new OrderByClause(SortOrder.Descending, "vigenciadesde"))
                .build();

        try {
            @SuppressWarnings("unchecked")
            List<Object> rawResult = consumer.query(DATASET_ID, query, Soda2Consumer.HASH_RETURN_TYPE);

            // Convert the raw result to the expected type
            List<Map<String, Object>> rows = (List<Map<String, Object>>) (List<?>) rawResult;

            if (rows.isEmpty()) {
                throw new RuntimeException("Error obteniendo TRM más reciente");
            }
            // Extraemos la fecha desde el campo vigenciadesde
            Map<String,Object> row = rows.get(0);
            String vigStr = (String) row.get("vigenciadesde");             // e.g. "2025-07-30T00:00:00.000"
            LocalDate fecha = LocalDate.parse(vigStr.substring(0, 10));    // yyyy-MM-dd

            return mapToTrm(row, fecha);
        } catch (SodaError | InterruptedException e) {
            log.error("Error obteniendo TRM más reciente: {}", e.getMessage(), e);
            throw new RuntimeException("Error obteniendo TRM más reciente", e);
        }
    }

    /** Convierte un Map SOQL → instancia de TRM */
    private TRM mapToTrm(Map<String,Object> row, LocalDate fechaParsed) {
        TRM trm = new TRM();
        trm.setFecha(fechaParsed);
        trm.setValor(new BigDecimal((String) row.get("valor")));
        trm.setUnidad((String) row.get("unidad"));
        trm.setVigenciaDesde((String) row.get("vigenciadesde"));
        trm.setVigenciaHasta((String) row.get("vigenciahasta"));
        return trm;
    }
}
