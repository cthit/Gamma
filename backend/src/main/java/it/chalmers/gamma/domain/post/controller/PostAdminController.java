package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupWithMembers;
import it.chalmers.gamma.util.domain.UserPost;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.post.controller.response.*;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.post.service.PostService;
import it.chalmers.gamma.domain.post.controller.request.AddPostRequest;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import it.chalmers.gamma.util.response.InputValidationFailedResponse;
import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
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
    private final PostFinder postFinder;
    private final MembershipFinder membershipFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostAdminController.class);

    public PostAdminController(PostService postService, PostFinder postFinder, MembershipFinder membershipFinder) {
        this.postService = postService;
        this.postFinder = postFinder;
        this.membershipFinder = membershipFinder;
    }

    /**
     * Adds a new post, eg ordförande or ledamot.
     *
     * @param request the name of the new post
     * @return what the result of trying to create the post was.
     */
    @PostMapping()
    public PostCreatedResponse addOfficialPost(@Valid @RequestBody AddPostRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        this.postService.create(
                new PostDTO(request.post, request.emailPrefix)
        );
        return new PostCreatedResponse();
    }

    /**
     * Attempts to edit the name of a already created post.
     *
     * @param request the new name of the post
     * @param id      the id of the post
     * @return the result of creating the post
     */
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
