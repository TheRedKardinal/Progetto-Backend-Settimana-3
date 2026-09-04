package claudiogiasi.progettosettimana3.dto;

import claudiogiasi.progettosettimana3.entities.Role;
import claudiogiasi.progettosettimana3.entities.User;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String username,
        String name,
        String surname,
        String email,
        Role role
) {
    // metodo from per rendere il codice più manutenibile
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRole()
        );
    }
}
