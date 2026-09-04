package claudiogiasi.progettosettimana3.services;

import claudiogiasi.progettosettimana3.dto.LoginRequestDTO;
import claudiogiasi.progettosettimana3.dto.RegisterRequestDTO;
import claudiogiasi.progettosettimana3.entities.Role;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.exceptions.BadRequestException;
import claudiogiasi.progettosettimana3.exceptions.NotFoundException;
import claudiogiasi.progettosettimana3.exceptions.UnauthorizedException;
import claudiogiasi.progettosettimana3.repositories.UserRepository;
import claudiogiasi.progettosettimana3.security.JwtTool;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTool jwtTool) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTool = jwtTool;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }

    public List<User> getAll() {
        return userRepository.findAll();
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

    public String login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUsername(loginRequestDTO.username())
                .orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));

        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenziali non valide");
        }

        return jwtTool.generateToken(user);
    }

    public User changeRole(UUID id, Role role) {
        User user = findById(id);
        user.changeRole(role);
        return userRepository.save(user);
    }
}
