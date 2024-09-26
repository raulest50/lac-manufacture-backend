package lacosmetics.planta.lacmanufacture.model.insumo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MP_dT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Insumo_MP_dT extends Insumo {

    @ManyToOne
    @JoinColumn(name = "terminado_id") // Foreign key column in Insumo_MP_dST table
    private Terminado terminado;

    @ManyToOne
    @JoinColumn(name = "referencia_mp_dt", referencedColumnName = "referencia")
    private MateriaPrima materiaPrima;

}
