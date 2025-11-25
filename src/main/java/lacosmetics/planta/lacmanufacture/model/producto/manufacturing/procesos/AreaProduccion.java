package lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AreaProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int areaId;

    @Column(unique = true)
    private String nombre;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private User responsableArea;
    
}
