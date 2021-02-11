package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.data.GroupWithMembersDTO;
import it.chalmers.gamma.domain.membership.data.MembershipRestrictedDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.membership.service.MembershipService;
import it.chalmers.gamma.domain.post.controller.response.*;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.post.service.PostService;
import it.chalmers.gamma.domain.post.controller.request.AddPostRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.group.controller.response.GetGroupResponse;
import it.chalmers.gamma.domain.post.controller.response.GetPostUsagesResponse.GetPostUsagesResponseObject;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
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

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
@RestController
@RequestMapping("/admin/groups/posts")
public final class PostAdminController {

    private final PostService postService;
    private final PostFinder postFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostAdminController.class);

    public PostAdminController(PostService postService, PostFinder postFinder) {
        this.postService = postService;
        this.postFinder = postFinder;
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
        this.postService.addPost(
                new PostDTO(request.getPost(), request.getEmailPrefix())
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
            @PathVariable("id") UUID id) {
        try {
            this.postService.editPost(new PostDTO(id, request.getPost(), request.getEmailPrefix()));
            return new PostEditedResponse();
        } catch (PostNotFoundException | IDsNotMatchingException e) {
            LOGGER.error("Post not found", e);
            throw new PostDoesNotExistResponse();
        }
    }


    @DeleteMapping("/{id}")
    public PostDeletedResponse deletePost(@PathVariable("id") UUID id) {
        try {
            this.postService.deletePost(id);
            return new PostDeletedResponse();
        } catch (PostNotFoundException e) {
            LOGGER.error("Post not found", e);
            throw new PostDoesNotExistResponse();
        }
    }

    @GetMapping("/{id}/usage")
    public GetPostUsagesResponseObject getPostUsages(@PathVariable("id") UUID postId) {
        return new GetPostUsagesResponse(this.postFinder.getPostUsages(postId)).toResponseObject();
    }
}
