package lacosmetics.planta.lacmanufacture.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoMP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMovimiento;

    @ManyToOne
    @JoinColumn(name = "grupo_movimiento_id")
    @JsonBackReference
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private GrupoMovimeintoMP grupoMovimeintoMP;

    @ManyToOne
    @JoinColumn(name = "referencia")
    @JsonBackReference
    @Getter(AccessLevel.NONE)
    //@Setter(AccessLevel.NONE)
    private MateriaPrima materiaPrima;

    private double cantidad;

    private int costo;


}
