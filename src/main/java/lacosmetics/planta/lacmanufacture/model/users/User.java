package lacosmetics.planta.lacmanufacture.model.users;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // 'user' can be a reserved keyword in some DBs
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private long cedula;

    @Column(unique = true) // username must be unique
    private String username;

    private String nombreCompleto;

    private String password;  // in a real app, store an encoded (hashed) password

    // opcional
    private String cel;

    //opcional
    private String direccion;

    //opcional
    private LocalDate fechaNacimiento;

    /**
     * 1: activo
     * 2: inactivo
     */
    private int estado;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

}
