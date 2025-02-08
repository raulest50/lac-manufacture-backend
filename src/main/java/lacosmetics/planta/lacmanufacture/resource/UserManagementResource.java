// src/main/java/lacosmetics/planta/lacmanufacture/resource/UserManagementResource.java
package lacosmetics.planta.lacmanufacture.resource;

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

    @PostMapping("/{userId}/roles")
    public ResponseEntity<User> addRoleToUser(
            @PathVariable Long userId,
            @RequestParam String roleName) {
        User updated = userManagementService.addRoleToUser(userId, roleName);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<User> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        User updated = userManagementService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(updated);
    }
}
