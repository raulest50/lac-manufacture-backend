package lacosmetics.planta.lacmanufacture.model.insumo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ST_dT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Insumo_ST_dT extends Insumo {

    @ManyToOne
    @JoinColumn(name = "terminado_id") // Foreign key column in Insumo_MP_dST table
    private Terminado terminado;

    @ManyToOne
    @JoinColumn(name = "referencia_st_dt", referencedColumnName = "referencia")
    private SemiTerminado semiTerminado;

}
