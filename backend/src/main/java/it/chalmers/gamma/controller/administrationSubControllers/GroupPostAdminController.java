package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.requests.AddPostRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addOfficialPost(@RequestBody AddPostRequest request) {
        if (postService.postExists(request.getPost().getSv())) {
            return new PostAlreadyExistsResponse();
        }
        postService.addPost(request.getPost());
        return new PostCreatedResponse();
    }
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

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getPosts() {
        return new GetMultiplePostsResponse(postService.getAllPosts());
    }
 /*   @RequestMapping(value = "/{id}/usage")
    public List<JSONObject> getPostUsages(@PathVariable("id") String id){
        List<FKITGroupSerializer.Properties> properties = FKITGroupSerializer.Properties.getAllProperties();
        Post post = postService.getPostById(id);
        List<UUID> groups = membershipService.getGroupsWithPost(post);
        for(UUID groupId : groups) {
            FKITGroup group = fkitService.getGroup(groupId);
            FKITGroup.FKITGroupView groupView = group.getView(props);
            List<ITUser> users = new ArrayList<>();
            List<UUID> userIDs = membershipService.getUserIdsByGroupAndPost(group, post);
            for(UUID userId: userIDs){
                users.add(itUserService.getUserById(userId));
            }
            groupView.setUsers(users);
            groupAndUser.add(groupView);
        }
        return new PostUsageResponse(groupAndUser);
    }*/
}
