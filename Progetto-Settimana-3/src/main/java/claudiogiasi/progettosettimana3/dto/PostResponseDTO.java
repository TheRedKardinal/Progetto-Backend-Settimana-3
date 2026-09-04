package claudiogiasi.progettosettimana3.dto;

import claudiogiasi.progettosettimana3.entities.Post;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponseDTO(
        UUID id,
        String testo,
        LocalDateTime dataPubblicazione,
        UUID authorId,
        String authorUsername
) {
    public static PostResponseDTO from(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getTesto(),
                post.getDataPubblicazione(),
                post.getUser().getId(),
                post.getUser().getUsername()
        );
    }
}
