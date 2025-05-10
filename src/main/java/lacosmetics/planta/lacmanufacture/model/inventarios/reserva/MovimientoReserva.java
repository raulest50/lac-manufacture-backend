package lacosmetics.planta.lacmanufacture.model.inventarios.reserva;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * - para modelar la reserva de materiales al momento de crear una orden
 * de produccion pero esta aun no ha sido liberada.
 * - no se espera consistencia stricta en esta tabla, se debe 
 * revisar consistencia de tanto en tanto.
 * - apropiada para guardar totales de reservas de materiales.
 * la idea es permitir update y eliminacion de records aqui
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoReserva {
    
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    /**
     * puede ser positivo o negativo
     */
    private int cantidad;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;


    /**
     * para saber si se trata de un movimiento en la tabla
     * para totalizar la cantidad de un producto o para
     * reservarlo.
     */
    private Tipo tipo;

    public enum Tipo {
        TOTALIZACION,
        RESERVA,
    }
    
}
