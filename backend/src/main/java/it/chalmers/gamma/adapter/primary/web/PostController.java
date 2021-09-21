package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.domain.post.Post;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/posts")
public final class PostController {

    private final PostFacade postFacade;

    public PostController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable("id") PostId id) {
        return this.postFacade.get(id)
                .orElseThrow(PostNotFoundResponse::new);
    }


    @GetMapping()
    public List<Post> getPosts() {
        return this.postFacade.getAll();
    }

    private static class PostNotFoundResponse extends NotFoundResponse { }

}
