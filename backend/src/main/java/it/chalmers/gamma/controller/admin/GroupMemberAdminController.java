package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.Post;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.requests.AddUserGroupRequest;
import it.chalmers.gamma.requests.EditMembershipRequest;

import it.chalmers.gamma.response.EditedMembershipResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.membership.MemberAddedToGroupResponse;
import it.chalmers.gamma.response.UserNotFoundResponse;
import it.chalmers.gamma.response.membership.MemberRemovedFromGroupResponse;

import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;

import javax.validation.Valid;

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
    public MemberAddedToGroupResponse addUserToGroup(
            @Valid @RequestBody AddUserGroupRequest request, BindingResult result,
            @PathVariable("id") String id) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUserDTO user = this.itUserService.getUser(request.getUserId());
        FKITGroupDTO fkitGroup = this.fkitGroupService.getDTOGroup(id);
        PostDTO post = this.postService.getPostDTO(request.getPost());
        this.membershipService.addUserToGroup(fkitGroup, user, post, request.getUnofficialName());
        return new MemberAddedToGroupResponse();
    }

    @RequestMapping(value = "/{id}/members/{user}", method = RequestMethod.DELETE)
    public MemberRemovedFromGroupResponse deleteUserFromGroup(@PathVariable("id") String id,
                                                              @PathVariable("user") String userId) {
        FKITGroupDTO group = this.fkitGroupService.getDTOGroup(id);
        ITUserDTO user = this.itUserService.getUser(userId);
        this.membershipService.removeUserFromGroup(group, user);
        return new MemberRemovedFromGroupResponse();
    }

    @RequestMapping(value = "/{id}/members/{user}", method = RequestMethod.PUT)
    public EditedMembershipResponse editUserInGroup(@PathVariable("id") String groupId,
                                                  @PathVariable("user") String userId,
                                                  @Valid @RequestBody EditMembershipRequest request,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        FKITGroupDTO group = this.fkitGroupService.getDTOGroup(groupId);
        ITUserDTO user = this.itUserService.getUser(userId);
        MembershipDTO membership = this.membershipService.getMembershipByUserAndGroup(user, group);
        PostDTO post = this.postService.getPostDTO(request.getPost());
        this.membershipService.editMembership(membership, request.getUnofficialName(), post);
        return new EditedMembershipResponse();
    }
}
