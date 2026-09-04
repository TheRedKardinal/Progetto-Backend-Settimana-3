package claudiogiasi.progettosettimana3.repositories;

import claudiogiasi.progettosettimana3.entities.Like;
import claudiogiasi.progettosettimana3.entities.Post;
import claudiogiasi.progettosettimana3.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserAndPost(User user, Post post);

    Optional<Like> findByUserAndPost(User user, Post post);
}
