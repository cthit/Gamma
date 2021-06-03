package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.internal.post.service.PostDTO;
import it.chalmers.gamma.internal.post.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("userBootstrap")
@Component
public class PostBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostBootstrap.class);

    private final MockData mockData;
    private final PostService postService;

    public PostBootstrap(MockData mockData, PostService postService) {
        this.mockData = mockData;
        this.postService = postService;
    }

    @PostConstruct
    public void createPosts() {
        mockData.posts().forEach(mockPost ->
                this.postService.create(
                        new PostDTO(
                                mockPost.id(),
                                mockPost.postName(),
                                null
                        )
                )
        );
        LOGGER.info("Posts created");
    }

}
