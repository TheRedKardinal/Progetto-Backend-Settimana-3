package claudiogiasi.progettosettimana3.dto;

import claudiogiasi.progettosettimana3.entities.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequestDTO(
        @NotNull(message = "Il ruolo è obbligatorio")
        Role role
) {
}
