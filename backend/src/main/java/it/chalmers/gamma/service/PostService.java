package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.PostRepository;

import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.response.post.PostDoesNotExistResponse;
import it.chalmers.gamma.response.post.PostIsInUseResponse;
import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


/**
 * A class to handle all database access to memberships between users and groups in the system.
 */
@Service
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public PostDTO addPost(UUID id, Text postName) {
        Post post = new Post();
        if (id != null) {
            post.setId(id);
        }
        return savePost(post, postName);
    }

    public PostDTO addPost(Text postName) {
        return addPost(null, postName);
    }

    public PostDTO editPost(PostDTO post, Text text) {
        return savePost(this.getPost(post), text);
    }

    private PostDTO savePost(Post post, Text postName) {
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        return this.repository.save(post).toDTO();
    }

    public boolean postExists(String id) {
        if (UUIDUtil.validUUID(id)) {
            return this.repository.existsById(UUID.fromString(id));
        }
        return this.repository.existsByPostName_Sv(id);
    }

    public List<PostDTO> getAllPosts() {
        return this.repository.findAll().stream().map(Post::toDTO).collect(Collectors.toList());
    }

    public PostDTO getPostDTO(String post) {
        if (UUIDUtil.validUUID(post)) {
            return this.repository.findById(UUID.fromString(post))
                    .orElseThrow(PostDoesNotExistResponse::new).toDTO();
        }
        return this.repository.findByPostName_Sv(post.toLowerCase())
                .orElseThrow(PostDoesNotExistResponse::new).toDTO();
    }

    public void deletePost(UUID id) {
        if (!this.postExists(id.toString())) {
            throw new PostDoesNotExistResponse();
        }

        this.repository.deleteById(id);
    }

    protected Post getPost(PostDTO postDTO) {
        return this.repository.findById(postDTO.getId()).orElse(null);
    }

}
