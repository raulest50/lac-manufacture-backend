package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="procesos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id", unique = true, updatable = true, nullable = false)
    private int procesoId;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subproceso_id")
    private List<SubProceso> subProcesos;



}
