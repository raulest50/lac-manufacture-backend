package lacosmetics.planta.lacmanufacture.model.producto.receta.procesoprod;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="process_nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_producto", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProcesoNode.class, name = "procesoNode"),
        @JsonSubTypes.Type(value = TargetNode.class, name = "targetNode"),
        @JsonSubTypes.Type(value = MateriaPrimaNode.class, name = "materialPrimarioNode")
})
public abstract class Node {

    /**
     * este es un id unico para la bd de sql
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = true, nullable = false)
    private int Id;

    /**
     * Este es un id que uso en el front puede repetirse en cada sesion, por eso le llamo
     * localId y por eso defino un primary key como un atributo diferente (Id attribute)
     */
    private String localId;

    private String label;
    private double x;
    private double y;

}
