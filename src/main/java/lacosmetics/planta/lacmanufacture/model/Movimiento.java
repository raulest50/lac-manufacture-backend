package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id", unique = true, updatable = false, nullable = false)
    private int movimiento_id;

    // puede ser positivo o negativo
    private double cantidad;

    // aplica para los 3 tipos de productos
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Lob
    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;

}
