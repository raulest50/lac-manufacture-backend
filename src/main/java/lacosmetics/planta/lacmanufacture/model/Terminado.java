package lacosmetics.planta.lacmanufacture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class Terminado extends Producto{

    // 0: 1er piso bodega llenado, 1: 2do piso llenado, 3r piso
    private int seccion_resposable;

    public List<TingredientPrima> tingredientPrimaList;

    public List<TingredientSemi> tingredientSemiList;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class TingredientPrima{

        @Id
        public String id;

        @ManyToOne
        public MateriaPrima materiaPrima;

        public double req_cantidad;
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class TingredientSemi{

        @Id
        public String id;

        @ManyToOne
        public SemiTerminado semiTerminado;

        public double req_cantidad;
    }
}
