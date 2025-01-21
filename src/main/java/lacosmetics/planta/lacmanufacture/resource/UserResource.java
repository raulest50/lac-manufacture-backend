package lacosmetics.planta.lacmanufacture.resource;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserResource {

    /**
     * If using HTTP Basic, you could check the principal to see if user is logged in
     */
    @GetMapping("/whoami")
    public Object whoAmI(Authentication authentication) {
        return authentication;
        // or return a custom DTO with roles, username, etc.
    }
}

