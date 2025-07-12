package lacosmetics.planta.lacmanufacture.model.activos.fijos.gestion;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Representa una categoría de activoFijos según su naturaleza.
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
     * Vida útil estimada en meses para esta categoría de activoFijos
     */
    private Integer vidaUtilMeses;

    /**
     * Método de depreciación predeterminado para esta categoría
     */
    private String metodoDespreciacionPredeterminado;

    @OneToMany(mappedBy = "categoria")
    private List<ActivoFijo> activoFijos;
}