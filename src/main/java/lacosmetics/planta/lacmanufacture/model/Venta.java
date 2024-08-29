package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id", unique = true, updatable = false, nullable = false)
    private int Id;

    @CreationTimestamp
    private LocalDateTime fechaVenta;
}
