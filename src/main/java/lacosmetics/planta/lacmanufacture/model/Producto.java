package lacosmetics.planta.lacmanufacture.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name="productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Producto {

    @Id
    @GeneratedValue(generator = "prefixed-id")
    @GenericGenerator(name = "prefixed-id", strategy = "lacosmetics.planta.lacmanufacture.config.PrefixedIdGenerator")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(length = 200)
    private String nombre;

    private String notas;

    @Min(value=0, message = "El costo no puede ser negativo") // validacion en db engine
    private int costo;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;



}


