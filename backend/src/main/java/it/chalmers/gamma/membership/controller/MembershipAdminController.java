package it.chalmers.gamma.membership.controller;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.post.PostService;
import it.chalmers.gamma.requests.AddUserGroupRequest;
import it.chalmers.gamma.requests.EditMembershipRequest;

import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.membership.controller.response.EditedMembershipResponse;
import it.chalmers.gamma.membership.controller.response.MemberAddedToGroupResponse;
import it.chalmers.gamma.membership.controller.response.MemberRemovedFromGroupResponse;

import it.chalmers.gamma.user.service.UserFinder;
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

    @PutMapping("/{groupId}/members/{userId}")
    public EditedMembershipResponse editUserInGroup(@PathVariable("groupId") UUID groupId,
                                                    @PathVariable("userId") UUID userId,
                                                    @Valid @RequestBody EditMembershipRequest request,
                                                    BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        this.membershipService.editMembership(groupId, userId, request.getUnofficialName(), request.getPostId());

        return new EditedMembershipResponse();
    }
}
