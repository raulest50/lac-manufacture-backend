package exotic.app.planta.model.organigrama;


import jakarta.persistence.*;
import exotic.app.planta.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Cargo {

    // Datos del modelo
    @Id
    private String idCargo;

    private String tituloCargo;

    private String descripcionCargo;

    private String departamento;

    private String urlDocManualFunciones;

    @OneToOne
    private User usuario;

    // datos de Node XyFLow/React
    private double posicionX;          // Posición X en el diagrama
    private double posicionY;          // Posición Y en el diagrama
    private int nivel;              // Nivel jerárquico
    private String jefeInmediato;     // ID Nodo al que reporta

}
