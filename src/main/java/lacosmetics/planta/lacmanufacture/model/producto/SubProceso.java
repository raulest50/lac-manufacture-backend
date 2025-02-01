package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="subprocesos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubProceso {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subproceso_id", unique = true, updatable = true, nullable = false)
    private int subprocesoId;



}
