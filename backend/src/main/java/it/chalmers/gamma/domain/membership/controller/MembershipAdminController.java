package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.membership.service.MembershipPK;
import it.chalmers.gamma.domain.membership.service.MembershipShallowDTO;
import it.chalmers.gamma.domain.membership.service.MembershipService;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.user.service.UserId;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/groups")
public final class MembershipAdminController {

    private final MembershipService membershipService;

    public MembershipAdminController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/{id}/members")
    public MemberAddedToGroupResponse addUserToGroup(
            @Valid @RequestBody AddUserGroupRequest request,
            @PathVariable("id") GroupId groupId) {
        this.membershipService.create(
                new MembershipShallowDTO(
                        request.postId,
                        groupId,
                        request.unofficialName,
                        request.userId
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
