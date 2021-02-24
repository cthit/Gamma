package it.chalmers.gamma.domain.post.service;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.GetAllEntities;
import it.chalmers.gamma.domain.GetEntity;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.post.data.Post;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.data.PostRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostFinder implements GetEntity<PostId, PostDTO>, GetAllEntities<PostDTO> {

    private final PostRepository postRepository;

    public PostFinder(PostRepository postRepository) {
        this.postRepository = postRepository;
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
