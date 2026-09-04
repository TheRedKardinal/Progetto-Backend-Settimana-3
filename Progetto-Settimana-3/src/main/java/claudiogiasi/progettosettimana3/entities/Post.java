package claudiogiasi.progettosettimana3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "post")
@Getter
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_post")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private User user;
    @Column(nullable = false)
    private String testo;
    @Column(name = "pubblicazione", nullable = false)
    private LocalDate pubblicazione;

    public Post() {
    }

    public Post(User user, String testo, LocalDate pubblicazione) {
        this.user = user;
        this.testo = testo;
        this.pubblicazione = pubblicazione;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
