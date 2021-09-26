package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.group.EmailPrefix;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.domain.post.PostId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("userBootstrap")
@Component
public class PostBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostBootstrap.class);

    private final MockData mockData;
    private final PostRepository postRepository;
    private final boolean mocking;

    public PostBootstrap(MockData mockData,
                         PostRepository postRepository,
                         @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.postRepository = postRepository;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createPosts() {
        if (!this.mocking || !this.postRepository.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== POST BOOTSTRAP ==========");

        mockData.posts().forEach(mockPost ->
                this.postRepository.create(
                        new Post(
                                new PostId(mockPost.id()),
                                new Text(
                                        mockPost.postName().sv(),
                                        mockPost.postName().en()
                                ),
                                EmailPrefix.empty()
                        )
                )
        );
        LOGGER.info("Posts created");
        LOGGER.info("========== POST BOOTSTRAP ==========");
    }

}
