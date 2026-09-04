package claudiogiasi.progettosettimana3.services;

import claudiogiasi.progettosettimana3.dto.PostRequestDTO;
import claudiogiasi.progettosettimana3.entities.Post;
import claudiogiasi.progettosettimana3.entities.Role;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.exceptions.NotFoundException;
import claudiogiasi.progettosettimana3.repositories.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post create(User author, PostRequestDTO dto) {
        Post post = new Post(author, dto.testo());
        return postRepository.save(post);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public Post getById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post non trovato"));
    }

    public Post update(UUID id, PostRequestDTO dto, User requester) {
        Post post = getById(id);

        boolean isAuthor = post.getUser().getId().equals(requester.getId());
        boolean isModerator = requester.getRole() == Role.MODERATOR;

        if (!isAuthor && !isModerator) {
            throw new AccessDeniedException("Puoi modificare solo i tuoi post");
        }

        post.setTesto(dto.testo());
        return postRepository.save(post);
    }
}
