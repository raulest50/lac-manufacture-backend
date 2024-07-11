package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="semi_terminados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemiTerminado extends Producto{

    // 0: 1er piso bodega llenado, 1: 2do piso llenado, 3r piso
    private int seccion_resposable;

    @OneToMany
    @JoinColumn(name = "singredient_id", referencedColumnName = "id") // This is optional but helps to specify the join column name
    private List<SIngredient> sIngredientList;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SIngredient{
        @Id
        private String id;

        private double req_cantidad;

        @ManyToOne
        @JoinColumn(name = "materia_prima_id")
        private MateriaPrima materiaPrima;
    }

}
