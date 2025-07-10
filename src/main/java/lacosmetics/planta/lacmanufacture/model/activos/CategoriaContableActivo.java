package lacosmetics.planta.lacmanufacture.model.activos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Representa una categoría de activos según su naturaleza.
 */
@Entity
@Table(name = "categoria_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaContableActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    /**
     * Vida útil estimada en meses para esta categoría de activos
     */
    private Integer vidaUtilMeses;

    /**
     * Método de depreciación predeterminado para esta categoría
     */
    private String metodoDespreciacionPredeterminado;

    @OneToMany(mappedBy = "categoria")
    private List<Activo> activos;
}