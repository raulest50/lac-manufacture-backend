package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// id assigned by the database ascendig order
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(length = 200)
    private String nombre;
    private String notas;

    @Min(value=0, message = "El costo no puede ser negativo") // validacion en db engine
    private int costo;


    private String tipo;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;


}
