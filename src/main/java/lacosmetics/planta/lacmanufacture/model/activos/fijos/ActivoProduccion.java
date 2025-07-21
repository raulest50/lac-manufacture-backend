package lacosmetics.planta.lacmanufacture.model.activos.fijos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivoProduccion extends ActivoFijo {

    private UnidadesCapacidad unidadesCapacidad;

    private double capacidad;

    public enum UnidadesCapacidad{
        L, // litros
        KG, // kilogramo
        TON, // tonelada
        M3, // metro cubico
        W, // potencia watts
        HP, // horse power
    }
}


