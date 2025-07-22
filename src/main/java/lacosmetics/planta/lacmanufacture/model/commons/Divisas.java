package lacosmetics.planta.lacmanufacture.model.commons;

import jakarta.persistence.Transient;
import lombok.Data;


@Data
public class Divisas {

    public enum DIVISAS{
        COP,
        USD,
    }
}
