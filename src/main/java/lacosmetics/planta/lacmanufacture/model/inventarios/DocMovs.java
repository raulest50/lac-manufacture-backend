package lacosmetics.planta.lacmanufacture.model.inventarios;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.inventarios.formatos.IngresoOCM;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "docs_movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_doc", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipo_doc"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IngresoOCM.class, name = "OC_IN"),
})
public abstract class DocMovs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_ingreso_id", unique = true, updatable = false, nullable = false)
    private int docIngresoId;

    @OneToMany(mappedBy = "documentoMovimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Movimientos> itemsDocIngreso;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;

    /**
     * url de la foto, scan o documento fisico de soporte si lo hay
     */
    private String urlDocSoporte;


    private User user;

    private String observaciones;

    public enum Lugar{
        EXTERNO,
        ALMACEN_GENERAL,
        ALMACEN_PERDIDAS,
    }

}
