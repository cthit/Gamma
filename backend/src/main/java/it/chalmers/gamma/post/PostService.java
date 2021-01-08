package it.chalmers.gamma.post;

import it.chalmers.gamma.db.entity.Text;

import it.chalmers.gamma.domain.post.PostDTO;
import it.chalmers.gamma.post.response.PostDoesNotExistResponse;
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

    public PostDTO addPost(Text postName) {
        return this.repository.save(new Post(postName, null)).toDTO();
    }

    public PostDTO addPost(Text postName, String emailPrefix) {
        return this.repository.save(new Post(postName, emailPrefix)).toDTO();
    }

    public PostDTO addPost(UUID id, Text postName, String emailPrefix) {
        Post post = new Post(postName, emailPrefix);
        post.setId(id);
        return this.repository.save(post).toDTO();
    }

    public PostDTO editPost(PostDTO postDTO, Text postName) {
        Post post = this.getPost(postDTO);
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        post.setEmailPrefix("");
        return this.repository.save(post).toDTO();
    }

    public PostDTO editPost(PostDTO postDTO, Text postName, String emailPrefix) {
        Post post = this.getPost(postDTO);
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        post.setEmailPrefix(emailPrefix);
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

    public Post getPost(PostDTO postDTO) {
        return this.repository.findById(postDTO.getId()).orElse(null);
    }

}
