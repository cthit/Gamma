package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.controller.response.MembershipNotFoundResponse;
import it.chalmers.gamma.domain.membership.data.MembershipShallowDTO;
import it.chalmers.gamma.domain.membership.exception.MembershipNotFoundException;
import it.chalmers.gamma.domain.membership.service.MembershipService;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.domain.post.service.PostService;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.requests.AddUserGroupRequest;
import it.chalmers.gamma.requests.EditMembershipRequest;

import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.membership.controller.response.EditedMembershipResponse;
import it.chalmers.gamma.domain.membership.controller.response.MemberAddedToGroupResponse;
import it.chalmers.gamma.domain.membership.controller.response.MemberRemovedFromGroupResponse;

import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin/groups")
public final class MembershipAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipAdminController.class);

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
            @PathVariable("id") UUID groupId) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            this.membershipService.addUserToGroup(
                    new MembershipShallowDTO(
                            request.getPostId(),
                            groupId,
                            request.getUnofficialName(),
                            request.getUserId()
                    )
            );
        } catch (GroupNotFoundException e) {
            LOGGER.error("Group not found when creating membership", e);
            throw new GroupDoesNotExistResponse();
        } catch (PostNotFoundException e) {
            LOGGER.error("Post not found when creating membership", e);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        return new MemberAddedToGroupResponse();
    }

    @DeleteMapping("/{id}/members/{user}")
    public MemberRemovedFromGroupResponse deleteUserFromGroup(@PathVariable("id") UUID groupId,
                                                              @PathVariable("user") UUID userId) {
        try {
            this.membershipService.removeUserFromGroup(groupId, userId);
            return new MemberRemovedFromGroupResponse();
        } catch (MembershipNotFoundException e) {
            LOGGER.error("Membership not found", e);
            throw new MembershipNotFoundResponse();
        }
    }

    @PutMapping("/{groupId}/members/{userId}")
    public EditedMembershipResponse editUserInGroup(@PathVariable("groupId") UUID groupId,
                                                    @PathVariable("userId") UUID userId,
                                                    @Valid @RequestBody EditMembershipRequest request,
                                                    BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            this.membershipService.editMembership(
                    new MembershipShallowDTO(
                            request.getPostId(),
                            groupId,
                            request.getUnofficialName(),
                            userId
                    )
            );
        } catch (MembershipNotFoundException e) {
            e.printStackTrace();
        } catch (IDsNotMatchingException e) {
            e.printStackTrace();
        } catch (GroupNotFoundException e) {
            e.printStackTrace();
        } catch (PostNotFoundException e) {
            e.printStackTrace();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        return new EditedMembershipResponse();
    }
}
