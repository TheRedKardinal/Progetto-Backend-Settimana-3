package claudiogiasi.progettosettimana3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"id_utente", "id_post"}))
@Getter
@ToString
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_like")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "id_post", nullable = false)
    private Post post;

    public Like() {
    }

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
