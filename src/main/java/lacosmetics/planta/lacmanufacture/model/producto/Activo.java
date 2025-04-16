package lacosmetics.planta.lacmanufacture.model.producto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Activo {

    // llave primaria
    private String id;

    private String nombre;

    /**
     * diferente de proveedor. ej:
     * un proveedor puede venter laptop Dell pero no es el fabricante
     */
    private String brand;

    /**
     * 0: activo
     * 1: obsoleto
     * 2: dado de baja
     */
    private int estado;

    private LocalDateTime fechaCodificacion;

    private LocalDateTime fechaBaja;


}
