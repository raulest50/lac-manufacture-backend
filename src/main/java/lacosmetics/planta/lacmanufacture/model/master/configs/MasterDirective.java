package lacosmetics.planta.lacmanufacture.model.master.configs;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MasterDirective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * nombre de la configuracion
     */
    @Column(unique = true)
    private String nombre;

    /**
     * una descripcion corta de que hace
     */
    private String resumen;

    /**
     * para soportar cualquier tipo,
     * booleano, TRUE, FALSE, 1020050 (topes monetarios), cualquier cosa
     */
    private String valor;


    @Enumerated(EnumType.STRING)
    private GRUPO grupo;

    /**
     * descripcion mas detallada de que hace esta configuracion.
     */
    private String ayuda;

    public enum GRUPO {
        FLEXIBILIDAD_CONTROL,
    }

}
