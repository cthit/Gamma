package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.requests.AddPostRequest;
import it.chalmers.gamma.response.EditedPostResponse;
import it.chalmers.gamma.response.GetMultiplePostsResponse;
import it.chalmers.gamma.response.GetPostResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.PostAlreadyExistsResponse;
import it.chalmers.gamma.response.PostCreatedResponse;
import it.chalmers.gamma.response.PostDeletedResponse;
import it.chalmers.gamma.response.PostDoesNotExistResponse;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addOfficialPost(@Valid @RequestBody AddPostRequest request, BindingResult result) {
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
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editPost(
            @RequestBody AddPostRequest request,
            @PathVariable("id") String id) {
        Post post = this.postService.getPost(UUID.fromString(id));
        if (post == null) {
            throw new PostDoesNotExistResponse();

        }
        this.postService.editPost(post, request.getPost());
        return new EditedPostResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Post> getPost(@PathVariable("id") String id) {
        Post post = this.postService.getPost(UUID.fromString(id));
        if (post == null) {
            throw new PostDoesNotExistResponse();
        }
        return new GetPostResponse(post);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePost(@PathVariable("id") String id) {
        if (this.postService.postExists(UUID.fromString(id))) {
            throw new PostDoesNotExistResponse();
        }
        this.postService.deletePost(UUID.fromString(id));
        return new PostDeletedResponse();
    }

    /**
     * gets all posts in the system.
     *
     * @return all posts currently in the system
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getPosts() {
        return new GetMultiplePostsResponse(this.postService.getAllPosts());
    }

    /**
     * gets all places where a post is used, meaning which groups have the post and who currently is assigned that post.
     *
     * @param id the ID of the post
     * @return a list of groups that has the post and who in the group currently is assigned that post
     */
    @RequestMapping("/{id}/usage")
    public List<JSONObject> getPostUsages(@PathVariable("id") String id) {
        if (this.postService.postExists(id)) {
            throw new PostDoesNotExistResponse();
        }
        List<ITUserSerializer.Properties> itUserProperties = Arrays.asList(
                ITUserSerializer.Properties.CID,
                ITUserSerializer.Properties.NICK,
                ITUserSerializer.Properties.ID,
                ITUserSerializer.Properties.FIRST_NAME,
                ITUserSerializer.Properties.LAST_NAME);
        List<FKITGroupSerializer.Properties> fkitGroupProperties = Arrays.asList(
                FKITGroupSerializer.Properties.PRETTY_NAME,
                FKITGroupSerializer.Properties.NAME,
                FKITGroupSerializer.Properties.ID,
                FKITGroupSerializer.Properties.USERS);
        Post post = this.postService.getPost(UUID.fromString(id));
        List<FKITGroup> groups = this.membershipService.getGroupsWithPost(post);
        ITUserSerializer itUserSerializer = new ITUserSerializer(itUserProperties);
        FKITGroupSerializer fkitGroupSerializer = new FKITGroupSerializer(fkitGroupProperties);

        // Everything above this is just initialization things.
        List<JSONObject> groupAndUser = new ArrayList<>();
        for (FKITGroup group : groups) {
            List<ITUser> userIDs = this.membershipService.getUserByGroupAndPost(group, post);
            List<JSONObject> users = new ArrayList<>();
            for (ITUser user : userIDs) {
                users.add(itUserSerializer.serialize(user, null));
            }
            groupAndUser.add(fkitGroupSerializer.serialize(group, users, null));
        }
        return groupAndUser;
    }
}
