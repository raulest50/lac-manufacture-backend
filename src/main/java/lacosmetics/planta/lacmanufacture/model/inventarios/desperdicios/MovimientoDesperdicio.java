package lacosmetics.planta.lacmanufacture.model.inventarios.desperdicios;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.repository.cdi.Eager;

import java.time.LocalDateTime;

@Entity(name="movimientosAlmacenDesperdicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDesperdicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id", unique = true, updatable = false, nullable = false)
    private int movimientoId;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;



}
