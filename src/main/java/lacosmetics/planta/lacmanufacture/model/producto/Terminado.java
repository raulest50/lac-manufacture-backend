package lacosmetics.planta.lacmanufacture.model.producto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lacosmetics.planta.lacmanufacture.model.insumo.Insumo_MP_dT;
import lacosmetics.planta.lacmanufacture.model.insumo.Insumo_ST_dT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Terminado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referencia", unique = true, updatable = false, nullable = false)
    private int referencia;

    private String descripcion;

    private int costo;

    private double cantidad;

    private String tipoUnidades;
    private double contenidoPorUnidad;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    private String observaciones;

    @OneToMany(mappedBy = "terminado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Insumo_MP_dT> insumosMP;

    @OneToMany(mappedBy = "terminado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Insumo_ST_dT> insumoST;

}
