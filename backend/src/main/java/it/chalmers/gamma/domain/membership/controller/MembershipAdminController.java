package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.membership.controller.response.MembershipNotFoundResponse;
import it.chalmers.gamma.domain.membership.data.db.MembershipPK;
import it.chalmers.gamma.domain.membership.data.dto.MembershipShallowDTO;
import it.chalmers.gamma.domain.membership.service.MembershipService;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.domain.post.service.PostService;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.membership.controller.request.AddUserGroupRequest;
import it.chalmers.gamma.domain.membership.controller.request.EditMembershipRequest;

import it.chalmers.gamma.util.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.membership.controller.response.EditedMembershipResponse;
import it.chalmers.gamma.domain.membership.controller.response.MemberAddedToGroupResponse;
import it.chalmers.gamma.domain.membership.controller.response.MemberRemovedFromGroupResponse;

import it.chalmers.gamma.domain.user.service.UserFinder;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/groups")
public final class MembershipAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipAdminController.class);

    private final MembershipService membershipService;

    public MembershipAdminController(UserFinder userFinder,
                                     PostService postService,
                                     GroupService groupService,
                                     MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/{id}/members")
    public MemberAddedToGroupResponse addUserToGroup(
            @Valid @RequestBody AddUserGroupRequest request,
            @PathVariable("id") GroupId groupId) {
        this.membershipService.create(
                new MembershipShallowDTO(
                        request.getPostId(),
                        groupId,
                        request.getUnofficialName(),
                        request.getUserId()
                )
        );

        return new MemberAddedToGroupResponse();
    }

    @DeleteMapping("/{groupId}/members")
    public MemberRemovedFromGroupResponse deleteUserFromGroup(@PathVariable("groupId") GroupId groupId,
                                                              @RequestParam("userId") UserId userId,
                                                              @RequestParam("postId") PostId postId) {
        try {
            this.membershipService.delete(new MembershipPK(postId, groupId, userId));
            return new MemberRemovedFromGroupResponse();
        } catch (EntityNotFoundException e) {
            throw new MembershipNotFoundResponse();
        }
    }

    @PutMapping("/{groupId}/members")
    public EditedMembershipResponse editUserInGroup(@PathVariable("groupId") GroupId groupId,
                                                    @RequestParam("userId") UserId userId,
                                                    @RequestParam("postId") PostId postId,
                                                    @Valid @RequestBody EditMembershipRequest request) {
        try {
            this.membershipService.update(
                    new MembershipShallowDTO(
                            postId,
                            groupId,
                            request.unofficialName,
                            userId
                    )
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        return new EditedMembershipResponse();
    }
}
