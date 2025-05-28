// src/main/java/lacosmetics/planta/lacmanufacture/resource/UserManagementResource.java
package lacosmetics.planta.lacmanufacture.resource;

import lacosmetics.planta.lacmanufacture.model.users.Acceso;
import lacosmetics.planta.lacmanufacture.model.users.User;
import lacosmetics.planta.lacmanufacture.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserManagementResource {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userManagementService.createUser(user);
        return ResponseEntity.created(URI.create("/usuarios/" + created.getId())).body(created);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        User updated = userManagementService.updateUser(userId, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userManagementService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/accesos")
    public ResponseEntity<User> addAccesoToUser(
            @PathVariable Long userId,
            @RequestParam String moduloName) {
        User updated = userManagementService.addAccesoToUser(userId, moduloName);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}/accesos/{accesoId}")
    public ResponseEntity<User> removeAccesoFromUser(
            @PathVariable Long userId,
            @PathVariable Long accesoId) {
        User updated = userManagementService.removeAccesoFromUser(userId, accesoId);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{userId}/accesos/modulo")
    public ResponseEntity<User> addAccesoToUserByModulo(
            @PathVariable Long userId,
            @RequestParam Acceso.Modulo modulo,
            @RequestParam(defaultValue = "1") int nivel) {
        User updated = userManagementService.addAccesoToUserByModulo(userId, modulo, nivel);
        return ResponseEntity.ok(updated);
    }
}
