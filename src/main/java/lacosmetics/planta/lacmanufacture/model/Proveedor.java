package lacosmetics.planta.lacmanufacture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id; //  Nit

    private String nombre;

    private String direccion;

    /**
     * 0: Regimen comun
     * 1: Regimen simplificado
     * 2: Regimen especial
     */
    private int regimenTributario;

    private String ciudad;

    private String departamento;

    private String nombreContacto;

    private String telefono;

    private String email;

    private String url;

    private String observacion;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;


}
