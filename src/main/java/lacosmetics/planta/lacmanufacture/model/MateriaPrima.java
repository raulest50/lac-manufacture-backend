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
@DiscriminatorValue("M")
//@Table(name="materias_primas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima extends Producto {

    // 0:L, 1:KG, 2:U
    private int unit_type;

    @Min(value=0, message = "La Cantidad por unidad no puede ser negativa") // Cantidad por unidad
    private double cant_x_unidad;

}
