package it.chalmers.gamma.post;

import it.chalmers.gamma.post.response.PostDoesNotExistResponse;
import it.chalmers.gamma.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostFinder {

    private final PostRepository postRepository;

    public PostFinder(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean postExists(String id) {
        if (UUIDUtil.validUUID(id)) {
            return this.postRepository.existsById(UUID.fromString(id));
        }
        return this.postRepository.existsByPostName_Sv(id);
    }

    public List<PostDTO> getAllPosts() {
        return this.postRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<PostDTO> getPost(UUID id) {
        Optional<Post> postEntity = getPostEntity(id);
        Optional<PostDTO> post = Optional.empty();

        if(postEntity.isPresent()) {
            post = postEntity.map(this::toDTO);
        }

        return post;
    }

    //TODO make protected
    public Optional<Post> getPostEntity(UUID id) {
        return this.postRepository.findById(id);
    }

    //TODO Make protected
    public Optional<Post> getPostEntity(PostDTO postDTO) {
        return getPostEntity(postDTO.getId());
    }

    public PostDTO toDTO(Post post) {
        return new PostDTO(post.getId(), post.getPostName(), post.getEmailPrefix());
    }

}
