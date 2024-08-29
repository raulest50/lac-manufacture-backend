package lacosmetics.planta.lacmanufacture.model;

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
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int Id;
    private String tipoId;

    private String razonSocial;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;



}