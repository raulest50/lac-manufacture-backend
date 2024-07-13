package lacosmetics.planta.lacmanufacture.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("T")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Terminado extends SemiTerminado{

    // 0: standard, active ,   1: obsoleto, deprecated
    private int status;

}
