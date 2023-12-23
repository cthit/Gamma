package it.chalmers.gamma.app.post;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

@Service
public class PostFacade extends Facade {

    private final PostRepository postRepository;
    private final GroupRepository groupRepository;

    public PostFacade(AccessGuard accessGuard,
                      PostRepository postRepository,
                      GroupRepository groupRepository) {
        super(accessGuard);
        this.postRepository = postRepository;
        this.groupRepository = groupRepository;
    }

    public void create(NewPost newPost) {
        this.postRepository.save(
                new Post(
                        PostId.generate(),
                        0,
                        new Text(newPost.svText, newPost.enText),
                        new EmailPrefix(newPost.emailPrefix)
                )
        );
    }

    public void update(UpdatePost updatePost) throws PostRepository.PostNotFoundException {
        Post oldPost = this.postRepository.get(new PostId(updatePost.postId))
                .orElseThrow();
        Post newPost = oldPost.with()
                .version(updatePost.version)
                .name(new Text(
                        updatePost.svText,
                        updatePost.enText
                ))
                .emailPrefix(new EmailPrefix(updatePost.emailPrefix))
                .build();

        this.postRepository.save(newPost);
    }

    public void delete(UUID postId) throws PostRepository.PostNotFoundException {
        this.postRepository.delete(new PostId(postId));
    }

    public Optional<PostDTO> get(UUID postId) {
        this.accessGuard.require(isSignedIn());

        return this.postRepository.get(new PostId(postId)).map(PostDTO::new);
    }

    public List<PostDTO> getAll() {
        this.accessGuard.require(isSignedIn());

        return this.postRepository.getAll()
                .stream()
                .map(PostDTO::new)
                .toList();
    }

    public List<GroupFacade.GroupWithMembersDTO> getPostUsages(UUID postId) {
        this.accessGuard.require(isAdmin());

        return this.groupRepository.getAllByPost(new PostId(postId))
                .stream()
                .map(GroupFacade.GroupWithMembersDTO::new)
                .toList();
    }

    public record NewPost(String svText, String enText, String emailPrefix) {
    }

    public record UpdatePost(UUID postId, int version, String svText, String enText, String emailPrefix) {
    }

    public record PostDTO(UUID id,
                          int version,
                          String svName,
                          String enName,
                          String emailPrefix) {
        public PostDTO(Post post) {
            this(post.id().value(),
                    post.version(),
                    post.name().sv().value(),
                    post.name().en().value(),
                    post.emailPrefix().value());
        }
    }

}
