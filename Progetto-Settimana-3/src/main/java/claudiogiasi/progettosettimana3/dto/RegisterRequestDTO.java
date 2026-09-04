package claudiogiasi.progettosettimana3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Lo username è obbligatorio!")
        @Size(min = 3, message = "Lo username deve contenere almeno 3 caratteri!")
        String username,

        @NotBlank(message = "Il nome è obbligatorio!")
        String name,

        @NotBlank(message = "Il cognome è obbligatorio!")
        String surname,

        @NotBlank(message = "L'indirizzo email è obbligatorio!")
        @Email(message = "L'indirizzo email inserito non è valido!")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
        String password
) {
}
