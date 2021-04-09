package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupWithMembers;
import it.chalmers.gamma.util.domain.UserPost;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.post.service.PostDTO;
import it.chalmers.gamma.domain.post.service.PostService;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/groups/posts")
public final class PostAdminController {

    private final PostService postService;
    private final MembershipFinder membershipFinder;

    public PostAdminController(PostService postService, MembershipFinder membershipFinder) {
        this.postService = postService;
        this.membershipFinder = membershipFinder;
    }

    @PostMapping()
    public PostCreatedResponse addOfficialPost(@Valid @RequestBody AddPostRequest request) {
        this.postService.create(
                new PostDTO(request.post, request.emailPrefix)
        );
        return new PostCreatedResponse();
    }

    @PutMapping("/{id}")
    public PostEditedResponse editPost(
            @RequestBody AddPostRequest request,
            @PathVariable("id") PostId id) {
        try {
            this.postService.update(new PostDTO(id, request.post, request.emailPrefix));
            return new PostEditedResponse();
        } catch (EntityNotFoundException e) {
            throw new PostDoesNotExistResponse();
        }
    }

    @DeleteMapping("/{id}")
    public PostDeletedResponse deletePost(@PathVariable("id") PostId id) {
        this.postService.delete(id);
        return new PostDeletedResponse();
    }

    @GetMapping("/{id}/usage")
    public GetPostUsagesResponse getPostUsages(@PathVariable("id") PostId postId) {
        List<GroupWithMembers> groups = this.membershipFinder.getGroupsWithPost(postId)
                .stream()
                .map(group -> {
                    try {
                        return new GroupWithMembers(
                                group,
                                this.membershipFinder.getMembershipsByGroupAndPost(group.getId(), postId)
                                        .stream()
                                        .map(membership -> new UserPost(
                                                new UserRestrictedDTO(membership.getUser()),
                                                membership.getPost()
                                        ))
                                        .collect(Collectors.toList())
                        );
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return new GetPostUsagesResponse(groups);
    }
}
