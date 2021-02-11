package it.chalmers.gamma.domain.post.service;

import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.data.GroupWithMembersDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.post.data.Post;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.data.PostRepository;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostFinder {

    private final PostRepository postRepository;
    private final MembershipFinder membershipFinder;

    public PostFinder(PostRepository postRepository, MembershipFinder membershipFinder) {
        this.postRepository = postRepository;
        this.membershipFinder = membershipFinder;
    }

    public boolean postExists(UUID id) {
        return this.postRepository.existsById(id);
    }

    public boolean postExistsBySvName(String svName) {
        return this.postRepository.existsByPostName_Sv(svName);
    }

    public List<PostDTO> getPosts() {
        return this.postRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PostDTO getPostBySvName(String svName) throws PostNotFoundException {
        return toDTO(getPostEntityBySvName(svName));
    }

    public PostDTO getPost(UUID id) throws PostNotFoundException {
        return toDTO(getPostEntity(id));
    }

    protected Post getPostEntity(UUID id) throws PostNotFoundException {
        return this.postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    protected Post getPostEntity(PostDTO postDTO) throws PostNotFoundException {
        return getPostEntity(postDTO.getId());
    }

    protected Post getPostEntityBySvName(String svName) throws PostNotFoundException {
        return this.postRepository.findByPostName_Sv(svName)
                .orElseThrow(PostNotFoundException::new);
    }

    public List<GroupWithMembersDTO> getPostUsages(UUID postId) {
        List<GroupDTO> groups = this.membershipFinder.getGroupsWithPost(postId);
        return groups.stream()
                        .map(group -> new GroupWithMembersDTO(group, this.membershipFinder.getUserByGroupAndPost(group.getId(), postId)))
                        .collect(Collectors.toList());
    }

    public PostDTO toDTO(Post post) {
        return new PostDTO(post.getId(), post.getPostName(), post.getEmailPrefix());
    }
}
