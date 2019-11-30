package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.requests.AddUserGroupRequest;
import it.chalmers.gamma.requests.EditMembershipRequest;

import it.chalmers.gamma.response.EditedMembershipResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.post.PostDoesNotExistResponse;
import it.chalmers.gamma.response.UserAddedToGroupResponse;
import it.chalmers.gamma.response.UserNotFoundResponse;
import it.chalmers.gamma.response.UserRemovedFromGroupResponse;

import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.InputValidationUtils;

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
        FKITGroup fkitGroup = this.fkitGroupService.getDTOGroup(UUID.fromString(id));
        Post post = this.postService.getPostDTO(UUID.fromString(request.getPost()));
        this.membershipService.addUserToGroup(fkitGroup, user, post, request.getUnofficialName());
        return new UserAddedToGroupResponse();
    }

    @RequestMapping(value = "/{id}/members/{user}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUserFromGroup(@PathVariable("id") String id,
                                                      @PathVariable("user") String userId) {
        FKITGroup group = this.fkitGroupService.getDTOGroup(UUID.fromString(id));
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
        FKITGroupDTO group = this.fkitGroupService.getDTOGroup(UUID.fromString(groupId));
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        ITUserDTO user = this.itUserService.getUserById(UUID.fromString(userId));
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        MembershipDTO membership = this.membershipService.getMembershipByUserAndGroup(user, group);
        Post post = this.postService.getPostDTO(UUID.fromString(request.getPost()));
        this.membershipService.editMembership(membership, request.getUnofficialName(), post);
        return new EditedMembershipResponse();
    }
}
