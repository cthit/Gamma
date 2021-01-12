package it.chalmers.gamma.factories;

import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.post.PostService;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockPostFactory {

    @Autowired
    private PostService postService;

    public PostDTO generatePost() {
        return new PostDTO(
                UUID.randomUUID(),
                GenerationUtils.generateText(),
                GenerationUtils.generateEmail()
        );
    }

    public PostDTO savePost(PostDTO post) {
        return this.postService.addPost(post.getPostName());
    }

}
