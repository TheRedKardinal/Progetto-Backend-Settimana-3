package claudiogiasi.progettosettimana3.services;

import claudiogiasi.progettosettimana3.dto.RegisterRequestDTO;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.exceptions.BadRequestException;
import claudiogiasi.progettosettimana3.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.existsByUsername(registerRequestDTO.username())) {
            throw new BadRequestException("Username già esistente");
        }

        if (userRepository.existsByEmail(registerRequestDTO.email())) {
            throw new BadRequestException("L'indirizzo email è già utilizzato da un altro utente");
        }

        String hashedPassword = passwordEncoder.encode(registerRequestDTO.password());

        User newUser = new User(
                registerRequestDTO.username(),
                registerRequestDTO.name(),
                registerRequestDTO.surname(),
                registerRequestDTO.email(),
                hashedPassword
        );

        return userRepository.save(newUser);
    }
}
