package claudiogiasi.progettosettimana3.controllers;

import claudiogiasi.progettosettimana3.dto.PostRequestDTO;
import claudiogiasi.progettosettimana3.dto.PostResponseDTO;
import claudiogiasi.progettosettimana3.entities.Post;
import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> create(@AuthenticationPrincipal User user, @Valid @RequestBody PostRequestDTO dto) {
        Post post = postService.create(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponseDTO.from(post));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAll() {
        List<PostResponseDTO> response = postService.getAll().stream()
                .map(PostResponseDTO::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getById(@PathVariable UUID id) {
        Post post = postService.getById(id);
        return ResponseEntity.ok(PostResponseDTO.from(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> update(@PathVariable UUID id, @AuthenticationPrincipal User user, @Valid @RequestBody PostRequestDTO dto) {
        Post post = postService.update(id, dto, user);
        return ResponseEntity.ok(PostResponseDTO.from(post));
    }
}
