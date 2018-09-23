package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.db.serializers.SerializerValue;
import it.chalmers.gamma.requests.AddPostRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.SerializerUtil;
import org.bouncycastle.LICENSE;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/groups/posts")
public class GroupPostAdminController {

    private PostService postService;
    private MembershipService membershipService;
    private FKITService fkitService;
    private ITUserService itUserService;

    public GroupPostAdminController(PostService postService, MembershipService membershipService, FKITService fkitService, ITUserService itUserService){
        this.postService = postService;
        this.membershipService = membershipService;
        this.fkitService = fkitService;
        this.itUserService = itUserService;
    }

    /**
     * Adds a new post, eg ordförande or ledamot
     * @param request the name of the new post
     * @return what the result of trying to create the post was.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addOfficialPost(@RequestBody AddPostRequest request) {
        if (postService.postExists(request.getPost().getSv())) {
            return new PostAlreadyExistsResponse();
        }
        postService.addPost(request.getPost());
        return new PostCreatedResponse();
    }

    /**
     * Attempts to edit the name of a already created post
     * @param request the new name of the post
     * @param id the id of the post
     * @return the result of creating the post
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editPost(@RequestBody AddPostRequest request, @PathVariable("id") String id){
        Post post = postService.getPostById(id);
        if(post == null){
            return new MissingRequiredFieldResponse("post");
        }
        postService.editPost(post, request.getPost());
        return new EditedPostResponse();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Post> getPost(@PathVariable("id") String id){
        Post post = postService.getPostById(id);
        return new GetPostResponse(post);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePost(@PathVariable("id") String id){
        postService.deletePost(UUID.fromString(id));
        return new PostDeletedResponse();
    }

    /**
     * gets all posts in the system
     * @return all posts currently in the system
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getPosts() {
        return new GetMultiplePostsResponse(postService.getAllPosts());
    }

    /**
     * gets all places where a post is used, meaning which groups have the post and who currently is assigned that post
     * @param id the ID of the post
     * @return a list of groups that has the post and who in the group currently is assigned that post
     */
    @RequestMapping(value = "/{id}/usage")
    public List<JSONObject> getPostUsages(@PathVariable("id") String id){
        List<ITUserSerializer.Properties> ITUserProperties = Arrays.asList(ITUserSerializer.Properties.CID,
                ITUserSerializer.Properties.NICK, ITUserSerializer.Properties.ID, ITUserSerializer.Properties.FIRST_NAME,
                ITUserSerializer.Properties.LAST_NAME);
        List<FKITGroupSerializer.Properties> FKITGroupProperties = Arrays.asList(FKITGroupSerializer.Properties.PRETTY_NAME,
                FKITGroupSerializer.Properties.NAME, FKITGroupSerializer.Properties.ID, FKITGroupSerializer.Properties.USERS);
        Post post = postService.getPostById(id);
        List<FKITGroup> groups = membershipService.getGroupsWithPost(post);
        ITUserSerializer itUserSerializer = new ITUserSerializer(ITUserProperties);
        FKITGroupSerializer fkitGroupSerializer = new FKITGroupSerializer(FKITGroupProperties);
        List<JSONObject> groupAndUser = new ArrayList<>();
        for(FKITGroup group : groups) {
            List<ITUser> userIDs = membershipService.getUserIdsByGroupAndPost(group, post);
            List<JSONObject> users = new ArrayList<>();
            for(ITUser user: userIDs){
                users.add(itUserSerializer.serialize(user, null));
            }
            groupAndUser.add(fkitGroupSerializer.serialize(group, users, null));
        }
        return groupAndUser;
    }
}
