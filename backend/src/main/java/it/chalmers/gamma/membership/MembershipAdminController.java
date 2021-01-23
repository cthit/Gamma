package it.chalmers.gamma.membership;

import it.chalmers.gamma.group.GroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserDTO;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.post.PostService;
import it.chalmers.gamma.requests.AddUserGroupRequest;
import it.chalmers.gamma.requests.EditMembershipRequest;

import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.membership.response.EditedMembershipResponse;
import it.chalmers.gamma.membership.response.MemberAddedToGroupResponse;
import it.chalmers.gamma.membership.response.MemberRemovedFromGroupResponse;

import it.chalmers.gamma.user.UserFinder;
import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
@RestController
@RequestMapping("/admin/groups")
public final class MembershipAdminController {

    private final UserFinder userFinder;
    private final PostService postService;
    private final GroupService groupService;
    private final MembershipService membershipService;

    public MembershipAdminController(UserFinder userFinder,
                                     PostService postService,
                                     GroupService groupService,
                                     MembershipService membershipService) {
        this.userFinder = userFinder;
        this.postService = postService;
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    @PostMapping("/{id}/members")
    public MemberAddedToGroupResponse addUserToGroup(
            @Valid @RequestBody AddUserGroupRequest request, BindingResult result,
            @PathVariable("id") String id) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = this.userFinder.getUser(UUID.fromString(request.getUserId()));
        GroupDTO fkitGroup = this.groupService.getGroup(id);
        PostDTO post = this.postService.getPostDTO(request.getPost());
        this.membershipService.addUserToGroup(fkitGroup, user, post, request.getUnofficialName());
        return new MemberAddedToGroupResponse();
    }

    @DeleteMapping("/{id}/members/{user}")
    public MemberRemovedFromGroupResponse deleteUserFromGroup(@PathVariable("id") String id,
                                                              @PathVariable("user") String userId) {
        GroupDTO group = this.groupService.getGroup(id);
        UserDTO user = this.userFinder.getUser(UUID.fromString(userId));
        this.membershipService.removeUserFromGroup(group, user);
        return new MemberRemovedFromGroupResponse();
    }

    @PutMapping("/{id}/members/{user}")
    public EditedMembershipResponse editUserInGroup(@PathVariable("id") String groupId,
                                                    @PathVariable("user") String userId,
                                                    @Valid @RequestBody EditMembershipRequest request,
                                                    BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        GroupDTO group = this.groupService.getGroup(groupId);
        UserDTO user = this.userFinder.getUser(UUID.fromString(userId));
        MembershipDTO membership = this.membershipService.getMembershipByUserAndGroup(user, group);
        PostDTO post = this.postService.getPostDTO(request.getPost());
        this.membershipService.editMembership(membership, request.getUnofficialName(), post);
        return new EditedMembershipResponse();
    }
}
