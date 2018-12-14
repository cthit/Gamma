package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.requests.AddUserGroupRequest;
import it.chalmers.gamma.response.CidNotFoundResponse;
import it.chalmers.gamma.response.GetMembershipsResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.PostDoesNotExistResponse;
import it.chalmers.gamma.response.UserAddedToGroupResponse;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RestController
@RequestMapping("/admin/groups")
public final class GroupMemberAdminController {
    private final ITUserService itUserService;
    private final PostService postService;
    private final FKITService fkitService;
    private final MembershipService membershipService;

    public GroupMemberAdminController(
            ITUserService itUserService,
            PostService postService,
            FKITService fkitService,
            MembershipService membershipService) {
        this.itUserService = itUserService;
        this.postService = postService;
        this.fkitService = fkitService;
        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.POST)
    public ResponseEntity<String> addUserToGroup(
            @RequestBody AddUserGroupRequest request,
            @PathVariable("id") String id) {
        if (!this.itUserService.userExists(request.getUser())) {
            throw new CidNotFoundResponse();
        }
        if (!this.postService.postExists(request.getPost())) {
            throw new PostDoesNotExistResponse();
        }
        ITUser user = this.itUserService.loadUser(request.getUser());
        FKITGroup fkitGroup = this.fkitService.getGroup(UUID.fromString(id));
        Post post = this.postService.getPost(request.getPost());
        this.membershipService.addUserToGroup(fkitGroup, user, post, request.getUnofficialName());
        return new UserAddedToGroupResponse();
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    public ResponseEntity<List<Membership>> getUsersInGroup(@PathVariable("id") String id) {
        FKITGroup group = this.fkitService.getGroup(UUID.fromString(id));
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        List<ITUser> members = this.membershipService.getUsersInGroup(group);
        List<Membership> groupMembers = new ArrayList<>();
        for (ITUser member : members) {
            groupMembers.add(this.membershipService.getMembershipByUserAndGroup(member, group));
        }
        return new GetMembershipsResponse(groupMembers);
    }
}
