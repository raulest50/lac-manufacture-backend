package lacosmetics.planta.lacmanufacture.model.inventarios;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
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
        @JsonSubTypes.Type(value = DocIngresoAlmacenOC.class, name = "OC_IN"),
})
public abstract class DocumentoMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_ingreso_id", unique = true, updatable = false, nullable = false)
    private int docIngresoId;

    @OneToMany(mappedBy = "documentoMovimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Movimiento> itemsDocIngreso;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;

    /**
     * url de la foto, scan o documento fisico de soporte si lo hay
     */
    private String urlDocSoporte;

    /**
     * nombre de la persona o usuario que crea el documento de ingreso
     */
    private String nombreResponsable;

    private String observaciones;

}
