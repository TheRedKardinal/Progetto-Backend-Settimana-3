package claudiogiasi.progettosettimana3.controllers;

import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.services.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Void> like(@AuthenticationPrincipal User user, @PathVariable UUID postId) {
        likeService.like(user, postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> unlike(@AuthenticationPrincipal User user, @PathVariable UUID postId) {
        likeService.unlike(user, postId);
        return ResponseEntity.noContent().build();
    }
}
