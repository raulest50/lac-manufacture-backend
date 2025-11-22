package lacosmetics.planta.lacmanufacture.model.compras;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;
    
    @Column(name = "id", unique = true, updatable = true, nullable = false)
    private String id; // Nit

    /**
     * 0: cedula de ciudadania
     * 1: nit
     */
    private int tipoIdentificacion;

    private String nombre;
    private String direccion;

    /**
     * 0: Regimen comun
     * 1: Regimen simplificado
     * 2: Regimen especial
     */
    private int regimenTributario;

    private String ciudad;
    private String departamento;

    /**
     * Instead of a single contacto, we store a list of JSON objects.
     * Each object can represent a contact with its own attributes.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> contactos;

    private String url;
    private String observacion;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    /**
     * 0: credito
     * 1: contado
     * condicion de pago is persisted both in orden compra and proveedor. this
     * way it is possible to change condicion de pago in time and also keeo a history
     * of condiciones de pago over time in case it is changed.
     */
    private String condicionPago;

    /**
     * categorias:
     * 0: Servicios Operativos
     * 1: Materias Primas
     * 2: Materiales de Empaque
     * 3: Servicios Administrativos
     * 4: Equipos y Otros Servicios
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "categorias")
    private int[] categorias;

    private String rutUrl;
    private String camaraUrl;

}
