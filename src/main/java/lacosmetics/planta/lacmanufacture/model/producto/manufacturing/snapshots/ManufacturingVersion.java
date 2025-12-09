package lacosmetics.planta.lacmanufacture.model.producto.manufacturing.snapshots;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ManufacturingVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //terminado o semiterminado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnore
    private Producto producto;

    private int versionNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String insumosJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String procesoProduccionJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String casePackJson;

}
