package claudiogiasi.progettosettimana3.services;

import claudiogiasi.progettosettimana3.entities.Like;
import claudiogiasi.progettosettimana3.entities.Post;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.exceptions.BadRequestException;
import claudiogiasi.progettosettimana3.exceptions.NotFoundException;
import claudiogiasi.progettosettimana3.repositories.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;

    public LikeService(LikeRepository likeRepository, PostService postService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
    }

    public void like(User user, UUID postId) {
        Post post = postService.getById(postId);
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new BadRequestException("Hai già messo like a questo post");
        }

        likeRepository.save(new Like(user, post));
    }

    public void unlike(User user, UUID postId) {
        Post post = postService.getById(postId);

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new NotFoundException("Non hai messo like a questo post"));

        likeRepository.delete(like);
    }
}
