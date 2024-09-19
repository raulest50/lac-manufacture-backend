package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima {

    @Id
    private int referencia;
    private String descripcion;
    private int costo;
    private double cantidad;

    private String tipoUnidades;
    private double contenidoPorUnidad;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    private String observaciones;
}
