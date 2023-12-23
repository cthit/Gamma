package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/posts")
public final class PostController {

    private final PostFacade postFacade;

    public PostController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @GetMapping("/{id}")
    public PostFacade.PostDTO getPost(@PathVariable("id") UUID id) {
        return this.postFacade.get(id)
                .orElseThrow(PostNotFoundResponse::new);
    }

    @GetMapping()
    public List<PostFacade.PostDTO> getPosts() {
        return this.postFacade.getAll();
    }

    private static class PostNotFoundResponse extends NotFoundResponse {
    }

}
