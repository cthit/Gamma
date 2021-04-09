package it.chalmers.gamma.domain.post.service;

import it.chalmers.gamma.util.domain.abstraction.EntityExists;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostFinder implements GetEntity<PostId, PostDTO>, GetAllEntities<PostDTO>, EntityExists<PostId> {

    private final PostRepository postRepository;

    public PostFinder(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean exists(PostId postId) {
        return this.postRepository.existsById(postId);
    }

    public List<PostDTO> getAll() {
        return this.postRepository.findAll().stream().map(Post::toDTO).collect(Collectors.toList());
    }

    public PostDTO getBySvName(String svName) throws EntityNotFoundException {
        return this.postRepository.findByPostName_Sv(svName)
                .orElseThrow(EntityNotFoundException::new).toDTO();
    }

    public PostDTO get(PostId id) throws EntityNotFoundException {
        return getEntity(id).toDTO();
    }

    protected Post getEntity(PostId id) throws EntityNotFoundException {
        return this.postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    protected Post getEntity(PostDTO postDTO) throws EntityNotFoundException {
        return getEntity(postDTO.getId());
    }
}
