package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.domain.common.Text;
import it.chalmers.gamma.app.domain.group.EmailPrefix;
import it.chalmers.gamma.app.domain.post.Post;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.port.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostFacade extends Facade {

    private final PostRepository postRepository;

    public PostFacade(AccessGuard accessGuard, PostRepository postRepository) {
        super(accessGuard);
        this.postRepository = postRepository;
    }

    public record NewPost(String svText, String enText, String emailPrefix) { }

    public void create(NewPost newPost) {
        this.postRepository.create(
                new Post(
                        PostId.generate(),
                        new Text(newPost.svText, newPost.enText),
                        new EmailPrefix(newPost.emailPrefix)
                )
        );
    }

    public record UpdatePost(UUID postId, String svText, String enText, String emailPrefix) { }

    public void update(UpdatePost updatePost) throws PostRepository.PostNotFoundException {
        Post post = this.postRepository.get(new PostId(updatePost.postId)).orElseThrow();
//        this.postRepository.save();
        throw new UnsupportedOperationException();
    }

    public void delete(UUID postId) throws PostRepository.PostNotFoundException {
        this.postRepository.delete(new PostId(postId));
    }

    public record PostDTO(UUID id, String svText, String enText, String emailPrefix) {
        public PostDTO(Post post) {
            this(post.id().value(),
                    post.name().sv().value(),
                    post.name().en().value(),
                    post.emailPrefix().value());
        }
    }

    public Optional<PostDTO> get(UUID postId) {
        accessGuard.requireSignedIn();
        return this.postRepository.get(new PostId(postId)).map(PostDTO::new);
    }

    public List<PostDTO> getAll() {
        accessGuard.requireSignedIn();
        return this.postRepository.getAll()
                .stream()
                .map(PostDTO::new)
                .toList();
    }

}
