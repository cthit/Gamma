package it.chalmers.delta.controller.admin;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.Membership;
import it.chalmers.delta.db.entity.Post;

import it.chalmers.delta.requests.AddUserGroupRequest;
import it.chalmers.delta.requests.EditMembershipRequest;

import it.chalmers.delta.response.EditedMembershipResponse;
import it.chalmers.delta.response.GroupDoesNotExistResponse;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.response.PostDoesNotExistResponse;
import it.chalmers.delta.response.UserAddedToGroupResponse;
import it.chalmers.delta.response.UserNotFoundResponse;
import it.chalmers.delta.response.UserRemovedFromGroupResponse;

import it.chalmers.delta.service.FKITGroupService;
import it.chalmers.delta.service.ITUserService;
import it.chalmers.delta.service.MembershipService;
import it.chalmers.delta.service.PostService;
import it.chalmers.delta.util.InputValidationUtils;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
@RestController
@RequestMapping("/admin/groups")
public final class GroupMemberAdminController {
    private final ITUserService itUserService;
    private final PostService postService;
    private final FKITGroupService fkitGroupService;
    private final MembershipService membershipService;

    public GroupMemberAdminController(
            ITUserService itUserService,
            PostService postService,
            FKITGroupService fkitGroupService,
            MembershipService membershipService) {
        this.itUserService = itUserService;
        this.postService = postService;
        this.fkitGroupService = fkitGroupService;
        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.POST)
    public ResponseEntity<String> addUserToGroup(
            @Valid @RequestBody AddUserGroupRequest request, BindingResult result,
            @PathVariable("id") String id) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(UUID.fromString(request.getUserId()))) {
            throw new UserNotFoundResponse();
        }
        if (!this.postService.postExists(UUID.fromString(request.getPost()))) {
            throw new PostDoesNotExistResponse();
        }
        ITUser user = this.itUserService.getUserById(UUID.fromString(request.getUserId()));
        FKITGroup fkitGroup = this.fkitGroupService.getGroup(UUID.fromString(id));
        Post post = this.postService.getPost(UUID.fromString(request.getPost()));
        this.membershipService.addUserToGroup(fkitGroup, user, post, request.getUnofficialName());
        return new UserAddedToGroupResponse();
    }

    @RequestMapping(value = "/{id}/members/{user}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUserFromGroup(@PathVariable("id") String id,
                                                      @PathVariable("user") String userId) {
        FKITGroup group = this.fkitGroupService.getGroup(UUID.fromString(id));
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        ITUser user = this.itUserService.getUserById(UUID.fromString(userId));
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        this.membershipService.removeUserFromGroup(group, user);
        return new UserRemovedFromGroupResponse();
    }

    @RequestMapping(value = "/{id}/members/{user}", method = RequestMethod.PUT)
    public ResponseEntity<String> editUserInGroup(@PathVariable("id") String groupId,
                                                  @PathVariable("user") String userId,
                                                  @Valid @RequestBody EditMembershipRequest request,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        FKITGroup group = this.fkitGroupService.getGroup(UUID.fromString(groupId));
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        ITUser user = this.itUserService.getUserById(UUID.fromString(userId));
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        Membership membership = this.membershipService.getMembershipByUserAndGroup(user, group);
        Post post = this.postService.getPost(UUID.fromString(request.getPost()));
        this.membershipService.editMembership(membership, request.getUnofficialName(), post);
        return new EditedMembershipResponse();
    }
}
