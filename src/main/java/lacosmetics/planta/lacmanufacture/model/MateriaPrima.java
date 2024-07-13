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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima extends Producto {


}
