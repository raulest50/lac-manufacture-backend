package lacosmetics.planta.lacmanufacture.model.insumo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@DiscriminatorValue("MP_dST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Insumo_MP_dST extends Insumo {

    @ManyToOne
    @JoinColumn(name = "semiterminado_id") // Foreign key column in Insumo_MP_dST table
    private SemiTerminado semiTerminado;

    @ManyToOne
    @JoinColumn(name = "referencia_mp_dst", referencedColumnName = "referencia")
    private MateriaPrima materiaPrima;

}
