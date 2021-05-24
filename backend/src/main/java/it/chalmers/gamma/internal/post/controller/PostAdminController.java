package it.chalmers.gamma.internal.post.controller;

import it.chalmers.gamma.domain.EmailPrefix;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.GroupWithMembers;
import it.chalmers.gamma.domain.UserPost;
import it.chalmers.gamma.internal.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.internal.post.service.PostDTO;
import it.chalmers.gamma.internal.post.service.PostService;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
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

    private record CreateOrEditPost(@Valid TextDTO post,
                                   @Valid EmailPrefix emailPrefix) { }

    @PostMapping()
    public PostCreatedResponse addPost(@Valid @RequestBody CreateOrEditPost request) {
        this.postService.create(
                new PostDTO(new PostId(), request.post(), request.emailPrefix())
        );
        return new PostCreatedResponse();
    }

    @PutMapping("/{id}")
    public PostEditedResponse editPost(
            @RequestBody CreateOrEditPost request,
            @PathVariable("id") PostId id) {
        try {
            this.postService.update(new PostDTO(id, request.post(), request.emailPrefix()));
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
    public List<GroupWithMembers> getPostUsages(@PathVariable("id") PostId postId) {
        return this.membershipFinder.getGroupsWithPost(postId)
                .stream()
                .map(group -> {
                    try {
                        return new GroupWithMembers(
                                group,
                                this.membershipFinder.getMembershipsByGroupAndPost(group.id(), postId)
                                        .stream()
                                        .map(membership -> new UserPost(
                                                new UserRestrictedDTO(membership.user()),
                                                membership.post()
                                        ))
                                        .collect(Collectors.toList())
                        );
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static class PostEditedResponse extends SuccessResponse { }

    private static class PostDeletedResponse extends SuccessResponse { }

    private static class PostCreatedResponse extends SuccessResponse {}

    private static class PostDoesNotExistResponse extends ErrorResponse {
        private PostDoesNotExistResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

}
