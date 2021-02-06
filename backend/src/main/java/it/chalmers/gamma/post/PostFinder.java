package it.chalmers.gamma.post;

import it.chalmers.gamma.post.exception.PostNotFoundException;
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

    public List<PostDTO> getPosts() {
        return this.postRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PostDTO getPost(UUID id) throws PostNotFoundException {
        return toDTO(getPostEntity(id));
    }

    //TODO make protected
    public Post getPostEntity(UUID id) throws PostNotFoundException {
        return this.postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    //TODO Make protected
    public Post getPostEntity(PostDTO postDTO) throws PostNotFoundException {
        return getPostEntity(postDTO.getId());
    }

    public PostDTO toDTO(Post post) {
        return new PostDTO(post.getId(), post.getPostName(), post.getEmailPrefix());
    }

}
