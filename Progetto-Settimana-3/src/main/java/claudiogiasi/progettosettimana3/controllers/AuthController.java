package claudiogiasi.progettosettimana3.controllers;

import claudiogiasi.progettosettimana3.dto.LoginRequestDTO;
import claudiogiasi.progettosettimana3.dto.LoginResponseDTO;
import claudiogiasi.progettosettimana3.dto.RegisterRequestDTO;
import claudiogiasi.progettosettimana3.dto.UserResponseDTO;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        User savedUser = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.from(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        String token = userService.login(dto);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
