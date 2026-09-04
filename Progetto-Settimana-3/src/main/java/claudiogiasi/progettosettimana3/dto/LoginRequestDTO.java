package claudiogiasi.progettosettimana3.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Lo username è obbligatorio!")
        String username,

        @NotBlank(message = "La password è obbligatoria!")
        String password
) {
}
