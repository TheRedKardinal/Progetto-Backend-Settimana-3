package claudiogiasi.progettosettimana3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
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
    @Column(name = "data_pubblicazione", nullable = false)
    private LocalDateTime dataPubblicazione;

    public Post() {
    }

    public Post(User user, String testo) {
        this.user = user;
        this.testo = testo;
    }

    @PrePersist
    private void onCreate() {
        this.dataPubblicazione = LocalDateTime.now();
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
