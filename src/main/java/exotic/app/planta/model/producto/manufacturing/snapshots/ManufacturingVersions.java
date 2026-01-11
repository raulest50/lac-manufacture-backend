package exotic.app.planta.model.producto.manufacturing.snapshots;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import exotic.app.planta.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Snapshot de los datos de manufactura que se guardan como JSON plano para los endpoints
 * de actualización (incluido {@code mod_mnfacturing_semiter}). A continuación se muestran
 * ejemplos de las cargas que deben serializarse/deserializarse en cada uno de los campos
 * según se trate de un terminado (incluye {@code casePackJson}) o un semiterminado
 * (omitiendo {@code casePackJson}).
 *
 * <p>Ejemplo para un <strong>terminado</strong> (incluye empaque/case pack):
 * <pre>
 * {
 *   "insumosJson": {
 *     "insumos": [
 *       {"productoId": "MP-001", "nombre": "Fragancia base", "tipo": "materia prima", "cantidadRequerida": 4.5, "unidad": "kg"},
 *       {"productoId": "SEMI-010", "nombre": "Concentrado A", "tipo": "semiterminado", "cantidadRequerida": 12, "unidad": "kg"}
 *     ]
 *   },
 *   "procesoProduccionJson": {
 *     "areaProduccionId": 3,
 *     "rendimientoTeorico": 1200,
 *     "procesosProduccion": [
 *       {
 *         "procesoId": 10,
 *         "nombre": "Mezclado",
 *         "model": "THROUGHPUT_RATE",
 *         "throughputUnitsPerSec": 0.8,
 *         "setUpTime": 600,
 *         "posicionX": 120,
 *         "posicionY": 80,
 *         "handles": [
 *           {"frontendHandleId": "mix-out", "type": "SOURCE", "position": "RIGHT", "label": "sale mezcla"}
 *         ]
 *       },
 *       {
 *         "procesoId": 11,
 *         "nombre": "Envasado",
 *         "model": "PER_UNIT",
 *         "secondsPerUnit": 1.2,
 *         "handles": [
 *           {"frontendHandleId": "fill-in", "type": "TARGET", "position": "LEFT"}
 *         ]
 *       }
 *     ],
 *     "connections": [
 *       {"sourceHandleId": "mix-out", "targetHandleId": "fill-in"}
 *     ],
 *     "diagramaJson": {"nodes": [], "edges": []}
 *   },
 *   "casePackJson": {
 *     "unitsPerCase": 12,
 *     "ean14": "12345678901234",
 *     "largoCm": 30.5,
 *     "anchoCm": 20.1,
 *     "altoCm": 18.9,
 *     "grossWeightKg": 5.25,
 *     "defaultForShipping": true,
 *     "insumosEmpaque": [
 *       {"productoId": "EMP-001", "nombre": "Caja master", "cantidadRequerida": 1},
 *       {"productoId": "EMP-050", "nombre": "Divisiones internas", "cantidadRequerida": 12}
 *     ]
 *   }
 * }
 * </pre>
 *
 * <p>Ejemplo para un <strong>semiterminado</strong> que consume {@code mod_mnfacturing_semiter}
 * (sin {@code casePackJson}):
 * <pre>
 * {
 *   "insumosJson": {
 *     "insumos": [
 *       {"productoId": "MP-020", "nombre": "Base oleosa", "tipo": "materia prima", "cantidadRequerida": 2.75, "unidad": "kg"}
 *     ]
 *   },
 *   "procesoProduccionJson": {
 *     "areaProduccionId": 2,
 *     "rendimientoTeorico": 500,
 *     "procesosProduccion": [
 *       {
 *         "procesoId": 12,
 *         "nombre": "Reacción",
 *         "model": "CONSTANT",
 *         "constantSeconds": 1800,
 *         "handles": [
 *           {"frontendHandleId": "react-out", "type": "SOURCE", "position": "RIGHT"}
 *         ]
 *       }
 *     ],
 *     "connections": []
 *   }
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "manufacturing_versions")
public class ManufacturingVersions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //terminado o semiterminado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnore
    private Producto producto;

    private int versionNumber;

    @Lob
    private String insumosJson;

    @Lob
    private String procesoProduccionJson;

    @Lob
    private String casePackJson;

}
