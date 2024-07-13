package lacosmetics.planta.lacmanufacture.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "producto_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "producto_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MateriaPrima.class, name = "M"),
        @JsonSubTypes.Type(value = SemiTerminado.class, name = "S")
})
public abstract class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id", unique = true, updatable = false, nullable = false)
    private int producto_id;

    @Column(length = 200)
    private String nombre;

    private String notas;

    @Min(value=0, message = "El costo no puede ser negativo") // validacion en db engine
    private int costo;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;



}


