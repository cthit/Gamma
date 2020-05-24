package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.requests.AddPostRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.group.GetFKITGroupResponse;
import it.chalmers.gamma.response.post.GetPostUsagesResponse;
import it.chalmers.gamma.response.post.GetPostUsagesResponse.GetPostUsagesResponseObject;
import it.chalmers.gamma.response.post.PostAlreadyExistsResponse;
import it.chalmers.gamma.response.post.PostCreatedResponse;
import it.chalmers.gamma.response.post.PostDeletedResponse;
import it.chalmers.gamma.response.post.PostDoesNotExistResponse;
import it.chalmers.gamma.response.post.PostEditedResponse;
import it.chalmers.gamma.response.post.PostIsInUseResponse;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.validation.Valid;

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
public final class GroupPostAdminController {

    private final PostService postService;
    private final MembershipService membershipService;

    public GroupPostAdminController(
            PostService postService,
            MembershipService membershipService) {
        this.postService = postService;
        this.membershipService = membershipService;
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
        if (this.postService.postExists(request.getPost().getSv())) {
            throw new PostAlreadyExistsResponse();
        }
        this.postService.addPost(request.getPost());
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
            @PathVariable("id") String id) {
        PostDTO post = this.postService.getPostDTO(id);
        this.postService.editPost(post, request.getPost());
        return new PostEditedResponse();
    }


    @DeleteMapping("/{id}")
    public PostDeletedResponse deletePost(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        if(membershipService.isPostUsed(uuid)){
            throw new PostIsInUseResponse();
        }

        this.postService.deletePost(uuid);
        return new PostDeletedResponse();
    }


    /**
     * gets all places where a post is used, meaning which groups have the post and who currently is assigned that post.
     *
     * @param id the GROUP_ID of the post
     * @return a list of groups that has the post and who in the group currently is assigned that post
     */
    @GetMapping("/{id}/usage")
    public GetPostUsagesResponseObject getPostUsages(@PathVariable("id") String id) {
        PostDTO post = this.postService.getPostDTO(id);
        List<FKITGroupDTO> groups = this.membershipService.getGroupsWithPost(post);
        List<GetFKITGroupResponse> groupResponses = groups.stream()
                .map(g -> new GetFKITGroupResponse(g,
                        this.membershipService.getUserDTOByGroupAndPost(g, post)))
                .collect(Collectors.toList());
        return new GetPostUsagesResponse(groupResponses).toResponseObject();
    }
}
