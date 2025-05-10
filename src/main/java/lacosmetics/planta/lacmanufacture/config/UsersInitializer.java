package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.users.Role;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.repo.usuarios.RoleRepository;
import lacosmetics.planta.lacmanufacture.repo.usuarios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UsersInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final String ROLE_MASTER = "ROLE_MASTER";
    private final String ROLE_COMPRAS = "ROLE_COMPRAS";
    private final String ROLE_JEFE_PRODUCCION = "ROLE_JEFE_PRODUCCION";
    private final String ROLE_ASISTENTE_PRODUCCION = "ROLE_ASISTENTE_PRODUCCION";
    private final String ROLE_ALMACEN = "ROLE_ALMACEN";
    private final String ROLE_BI = "ROLE_BI";
    private final String ROLE_ACTIVOS = "ROLE_ACTIVOS";

    public void initializeUsers() {
        Role roleMaster = initRole(ROLE_MASTER);
        initRole(ROLE_COMPRAS);
        initRole(ROLE_JEFE_PRODUCCION);
        initRole(ROLE_ASISTENTE_PRODUCCION);
        initRole(ROLE_ALMACEN);
        initRole(ROLE_BI);
        initRole(ROLE_ACTIVOS);

        userRepository.findByUsername("master").orElseGet(() -> {
            User master = User.builder()
                    .username("master")
                    .password("m1243") // Development only! (consider password encoding for production)
                    .roles(Set.of(roleMaster))
                    .build();
            return userRepository.save(master);
        });
    }

    private Role initRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = roleRepository.save(new Role(null, roleName));
        }
        return role;
    }
}
