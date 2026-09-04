package claudiogiasi.progettosettimana3.controllers;

import claudiogiasi.progettosettimana3.dto.UpdateRoleRequestDTO;
import claudiogiasi.progettosettimana3.dto.UserResponseDTO;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/utenti")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAll();
        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequestDTO dto) {
        User user = userService.changeRole(id, dto.role());
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }
}
