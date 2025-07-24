package lacosmetics.planta.lacmanufacture.model.users;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accesos")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    private int nivel; // nivel de acceso al modulo desde 1 en adelante

    @Enumerated(EnumType.STRING)
    private Modulo moduloAcceso;

    public enum Modulo{
        USUARIOS,
        PRODUCTOS,
        PRODUCCION,
        STOCK,
        PROVEEDORES,
        COMPRAS,
        SEGUIMIENTO_PRODUCCION,
        CLIENTES,
        VENTAS,
        TRANSACCIONES_ALMACEN,
        ACTIVOS,
        CONTABILIDAD,
        PERSONAL_PLANTA,
        BINTELLIGENCE,
        CARGA_MASIVA, // nuevos endpoints de aca para abajo
        ADMINISTRACION_ALERTAS,
        MASTER_CONFIGS,
        CRONOGRAMA,
        ORGANIGRAMA,
        PAGOS_PROVEEDORES
    }
}
