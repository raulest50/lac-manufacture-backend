package exotic.app.planta.model.ventas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id", unique = true, updatable = false, nullable = false)
    private int clienteId;

    private String nombre;
    private String email;
    private String telefono;
    private String direccion;

    private String condicionesPago;
    private Integer limiteCredito;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    private String urlRut;
    private String urlCamComer;
}
