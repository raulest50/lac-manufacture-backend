package lacosmetics.planta.lacmanufacture.model.producto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Familia {
    @Id
    @GeneratedValue
    private int familiaId;
    private String familiaNombre;
    private String familiaDescripcion;
}
