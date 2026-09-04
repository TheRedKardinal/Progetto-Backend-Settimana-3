package claudiogiasi.progettosettimana3.dto;

import jakarta.validation.constraints.NotBlank;

public record PostRequestDTO(
        @NotBlank(message = "Il testo del post è obbligatorio")
        String testo
) {
}
